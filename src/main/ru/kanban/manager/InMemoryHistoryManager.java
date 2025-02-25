package ru.kanban.manager;

import ru.kanban.entity.Task;

import java.util.ArrayList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {

    private final List<Task> historyList = new ArrayList<>();

    @Override
    public void add(Task task) {
        Task taskToPut = new Task(task);
        if (historyList.size() < 10) {
            historyList.add(taskToPut);
        } else {
            historyList.remove(0);
            historyList.add(task);
        }
    }

    @Override
    public List<Task> getHistory() {
        return new ArrayList<>(historyList);
    }

    @Override
    public void clearHistory() {
        historyList.clear();
    }
}
