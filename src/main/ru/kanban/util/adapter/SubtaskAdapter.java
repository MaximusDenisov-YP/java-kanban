package ru.kanban.util.adapter;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import ru.kanban.entity.Epic;
import ru.kanban.entity.Subtask;
import ru.kanban.entity.TaskStatus;
import ru.kanban.manager.TaskManager;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;

public class SubtaskAdapter extends TypeAdapter<Subtask> {
    private final TaskManager manager;

    public SubtaskAdapter(TaskManager manager) {
        this.manager = manager;
    }

    @Override
    public void write(JsonWriter writer, Subtask subtask) throws IOException {
        writer.beginObject();
        writer.name("id").value(subtask.getId());
        writer.name("name").value(subtask.getName());
        writer.name("description").value(subtask.getDescription());
        writer.name("status").value(subtask.getStatus().toString());
        writer.name("startTime").value(subtask.getStartTime().toString());
        writer.name("duration").value(subtask.getDuration().toString());
        writer.name("epicId").value(subtask.getEpic().getId());
        writer.endObject();
    }

    @Override
    public Subtask read(JsonReader reader) throws IOException {
        int id = 0;
        String name = null;
        String description = null;
        TaskStatus status = TaskStatus.NEW;
        LocalDateTime startTime = null;
        Duration duration = null;
        int epicId = -1;

        reader.beginObject();
        while (reader.hasNext()) {
            switch (reader.nextName()) {
                case "id" -> id = reader.nextInt();
                case "name" -> name = reader.nextString();
                case "description" -> description = reader.nextString();
                case "status" -> status = TaskStatus.valueOf(reader.nextString());
                case "startTime" -> startTime = LocalDateTime.parse(reader.nextString());
                case "duration" -> duration = Duration.parse(reader.nextString());
                case "epicId" -> epicId = reader.nextInt();
            }
        }
        reader.endObject();

        int finalEpicId = epicId;
        Epic epic = manager.getEpicById(epicId)
                .orElseThrow(() -> new IllegalArgumentException("Epic with ID " + finalEpicId + " not found"));
        Subtask subtask = new Subtask(name, description, epic, status, startTime, duration);
        subtask.setId(id);
        return subtask;
    }
}