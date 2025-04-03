package ru.kanban.manager;

import ru.kanban.entity.Epic;
import ru.kanban.entity.Subtask;
import ru.kanban.entity.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class InMemoryTaskManager implements TaskManager {

    private int id = 0;

    private final Map<Integer, Task> tasks = new HashMap<>();
    private final Map<Integer, Epic> epics = new HashMap<>();
    private final Map<Integer, Subtask> subtasks = new HashMap<>();

    private final HistoryManager historyManager = Managers.getDefaultHistory();

    public HistoryManager getHistoryManager() {
        return historyManager;
    }

    @Override
    public ArrayList<Task> getTasks() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public ArrayList<Subtask> getSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    @Override
    public ArrayList<Epic> getEpics() {
        return new ArrayList<>(epics.values());
    }

    @Override
    public void deleteAllTasks() {
        tasks.clear();
    }

    @Override
    public void deleteAllSubtasks() {
        for (Epic epic : epics.values()) {
            epic.clearSubtasks();
            epic.checkEpicStatus();
        }
        subtasks.clear();
    }

    @Override
    public void deleteAllEpics() {
        epics.clear();
        subtasks.clear();
    }

    @Override
    public Task getTaskById(int id) {
        Task result = tasks.get(id);
        if (result != null) {
            historyManager.add(result);
        }
        return result;
    }

    @Override
    public Epic getEpicById(int id) {
        Epic result = epics.get(id);
        if (result != null) {
            historyManager.add(result);
        }
        return result;
    }

    @Override
    public Subtask getSubtaskById(int id) {
        Subtask result = subtasks.get(id);
        if (result != null) {
            historyManager.add(result);
        }
        return result;
    }

    @Override
    public Task postTask(Task task) {
        int nextId = getNextId();
        if (isContainsTaskId(task)) {
            Task taskToPut = new Task(task);
            tasks.put(taskToPut.getId(), taskToPut);
            return taskToPut;
        } else {
            Task taskToPut = new Task(
                    task.getName(),
                    task.getDescription(),
                    nextId,
                    task.getStatus()
            );
            tasks.put(nextId, taskToPut);
            return taskToPut;
        }
    }

    @Override
    public Epic postEpic(Epic epic) {
        int nextId = getNextId();
        if (isContainsTaskId(epic)) {
            Epic epicToPut = new Epic(epic);
            return epics.put(epicToPut.getId(), epicToPut);
        } else {
            Epic epicToPut = new Epic(
                    epic.getName(),
                    epic.getDescription()
            );
            epicToPut.setId(nextId);
            return epics.put(nextId, epicToPut);
        }
    }

    @Override
    public Subtask postSubtask(Subtask subTask) {
        int nextId = getNextId();
        if (isContainsTaskId(subTask)) {
            Subtask subtaskToPut = new Subtask(subTask);
            epics.get(subtaskToPut.getEpic().getId()).putSubtaskToEpic(subtaskToPut);
            return subtasks.put(subTask.getId(), subtaskToPut);
        } else {
            Subtask subTaskToPut = new Subtask(
                    subTask.getName(),
                    subTask.getDescription(),
                    subTask.getEpic()
            );
            subTaskToPut.setId(nextId);
            subTaskToPut.getEpic().putSubtaskToEpic(subTaskToPut);
            return subtasks.put(nextId, subTaskToPut);
        }
    }

    @Override
    public Task updateTask(Task task) {
        if (tasks.containsKey(task.getId())) {
            tasks.get(task.getId()).setName(task.getName());
            tasks.get(task.getId()).setDescription(task.getDescription());
            tasks.get(task.getId()).setStatus(task.getStatus());
        }
        return task;
    }

    @Override
    public Epic updateEpic(Epic epic) {
        if (epics.containsKey(epic.getId())) {
            epics.get(epic.getId()).setName(epic.getName());
            epics.get(epic.getId()).setDescription(epic.getDescription());
            epics.get(epic.getId()).setStatus(epic.getStatus());
        }
        return epic;
    }

    @Override
    public Subtask updateSubtask(Subtask subTask) {
        if (subtasks.containsKey(subTask.getId())) {
            subtasks.get(subTask.getId()).setName(subTask.getName());
            subtasks.get(subTask.getId()).setDescription(subTask.getDescription());
            subtasks.get(subTask.getId()).setStatus(subTask.getStatus());
        }
        subTask.getEpic().checkEpicStatus();
        return subTask;
    }

    @Override
    public Task deleteTaskById(int id) {
        historyManager.remove(id);
        return tasks.remove(id);
    }

    @Override
    public Epic deleteEpicById(int id) {
        if (epics.get(id) != null) {
            ArrayList<Subtask> subTasksList = epics.get(id).getSubtaskArrayList();

            if (!subTasksList.isEmpty()) {
                for (Subtask subtask : subTasksList) {
                    historyManager.remove(subtask.getId());
                }
                subTasksList.clear();
            }
            if (epics.get(id).getSubtaskArrayList().isEmpty()) {
                historyManager.remove(id);
                return epics.remove(id);
            } else {
                throw new RuntimeException("Подзадачи эпика не были удалены.");
            }
        } else {
            return null;
        }
    }

    @Override
    public Subtask deleteSubtaskById(int id) {
        if (subtasks.get(id) != null) {
            subtasks.get(id).getEpic().getSubtaskArrayList().remove(subtasks.get(id));
            subtasks.get(id).getEpic().checkEpicStatus();
            historyManager.remove(id);
            return subtasks.remove(id);
        } else {
            return null;
        }
    }

    @Override
    public int getNextId() {
        return id++;
    }

    @Override
    public ArrayList<Subtask> getSubtasksFromEpic(Epic epic) {
        return epic.getSubtaskArrayList();
    }

    private boolean isContainsTaskId(Task task) {
        return !tasks.containsKey(task.getId()) && !epics.containsKey(task.getId()) && !subtasks.containsKey(task.getId());
    }
}