package ru.kanban.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import ru.kanban.entity.Epic;
import ru.kanban.entity.Subtask;
import ru.kanban.manager.TaskManager;
import ru.kanban.util.adapter.*;

import java.time.Duration;
import java.time.LocalDateTime;

public class GsonUtil {
    public static Gson getGson(TaskManager manager) {
        return new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .registerTypeAdapter(Duration.class, new DurationAdapter())
                .registerTypeAdapter(Subtask.class, new SubtaskAdapter(manager))
                .registerTypeAdapter(Epic.class, new EpicAdapter())
                .serializeNulls()
                .setPrettyPrinting()
                .create();
    }
}