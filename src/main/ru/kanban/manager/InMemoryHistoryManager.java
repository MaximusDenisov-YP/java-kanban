package ru.kanban.manager;

import ru.kanban.entity.Task;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {

    private final LinkedList<Task> historyList = new LinkedList<>();

    @Override
    public void add(Task task) {
        if (!(historyList.size() < 10)) {
            historyList.removeFirst();
        }
        historyList.add(new Task(task));
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
