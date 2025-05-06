package ru.kanban.util;

import com.google.gson.reflect.TypeToken;
import ru.kanban.entity.Epic;
import ru.kanban.entity.Subtask;
import ru.kanban.entity.Task;

import java.lang.reflect.Type;
import java.util.List;

public class JsonTypes {
    public static final Type TASK_LIST = new TypeToken<List<Task>>() {
    }.getType();
    public static final Type SUBTASK_LIST = new TypeToken<List<Subtask>>() {
    }.getType();
    public static final Type EPIC_LIST = new TypeToken<List<Epic>>() {
    }.getType();
}