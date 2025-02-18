package ru.kanban.manager;

import ru.kanban.entity.Task;

import java.util.List;

public interface HistoryManager {

    void add(Task task);

    List<Task> getHistory();

}
