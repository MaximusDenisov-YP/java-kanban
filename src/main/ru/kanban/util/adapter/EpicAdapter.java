package ru.kanban.util.adapter;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import ru.kanban.entity.Epic;
import ru.kanban.entity.TaskStatus;
import ru.kanban.entity.Subtask;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class EpicAdapter extends TypeAdapter<Epic> {
    @Override
    public void write(JsonWriter writer, Epic epic) throws IOException {
        writer.beginObject();
        writer.name("id").value(epic.getId());
        writer.name("name").value(epic.getName());
        writer.name("description").value(epic.getDescription());
        writer.name("status").value(epic.getStatus().name());
        if (epic.getStartTime() != null) {
            writer.name("startTime").value(epic.getStartTime().toString());
        } else {
            writer.name("startTime").nullValue();
        }
        if (epic.getEndTime() != null) {
            writer.name("endTime").value(epic.getEndTime().toString());
        } else {
            writer.name("endTime").nullValue();
        }
        if (epic.getDuration() != null) {
            writer.name("duration").value(epic.getDuration().toString());
        } else {
            writer.name("duration").nullValue();
        }
        writer.name("subtaskIds");
        writer.beginArray();
        for (Subtask subtask : epic.getSubtaskArrayList()) {
            writer.value(subtask.getId());
        }
        writer.endArray();

        writer.endObject();
    }

    @Override
    public Epic read(JsonReader reader) throws IOException {
        int id = -1;
        String name = null;
        String description = null;
        TaskStatus status = TaskStatus.NEW;
        LocalDateTime startTime = null;
        LocalDateTime endTime = null;
        Duration duration = null;
        ArrayList<Integer> subtaskIds = new ArrayList<>();

        reader.beginObject();
        while (reader.hasNext()) {
            switch (reader.nextName()) {
                case "id" -> id = reader.nextInt();
                case "name" -> name = reader.nextString();
                case "description" -> description = reader.nextString();
                case "status" -> status = TaskStatus.valueOf(reader.nextString());
                case "startTime" -> {
                    if (reader.peek().name().equals("NULL")) {
                        reader.nextNull();
                    } else {
                        startTime = LocalDateTime.parse(reader.nextString());
                    }
                }
                case "endTime" -> {
                    if (reader.peek().name().equals("NULL")) {
                        reader.nextNull();
                    } else {
                        endTime = LocalDateTime.parse(reader.nextString());
                    }
                }
                case "duration" -> {
                    if (reader.peek().name().equals("NULL")) {
                        reader.nextNull();
                    } else {
                        duration = Duration.parse(reader.nextString());
                    }
                }
                case "subtaskIds" -> {
                    reader.beginArray();
                    while (reader.hasNext()) {
                        subtaskIds.add(reader.nextInt());
                    }
                    reader.endArray();
                }
                default -> reader.skipValue();
            }
        }
        reader.endObject();
        Epic epic = new Epic(name, description, id);
        epic.setStatus(status);
        epic.setStartTime(startTime);
        epic.setEndTime(endTime);
        epic.setDuration(duration);
        return epic;
    }
}