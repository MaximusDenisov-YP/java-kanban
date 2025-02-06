package main.java.ru.kanban.manager;

import main.java.ru.kanban.entity.Epic;
import main.java.ru.kanban.entity.SubTask;
import main.java.ru.kanban.entity.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class TaskManager {
    private static int id = 0;

    private final Map<Integer, Task> tasks = new HashMap<>();
    private final Map<Integer, Epic> epics = new HashMap<>();
    private final Map<Integer, SubTask> subtasks = new HashMap<>();

    public ArrayList<Task> getTasks() {
        return new ArrayList<>(tasks.values());
    }

    public ArrayList<SubTask> getSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    public ArrayList<Epic> getEpics() {
        return new ArrayList<>(epics.values());
    }

    public void deleteAllTasks() {
        tasks.clear();
    }

    public void deleteAllSubtasks() {
        for (Epic epic : epics.values()) {
            epic.clearSubTasks();
            epic.checkEpicStatus();
        }
        subtasks.clear();
    }

    public void deleteAllEpics() {
        epics.clear();
        subtasks.clear();
    }

    public Task getTaskById(int id) {
        return tasks.get(id);
    }

    public Epic getEpicById(int id) {
        return epics.get(id);
    }

    public SubTask getSubTaskById(int id) {
        return subtasks.get(id);
    }

    public Task postTask(Task task) {
        int nextId = getNextId();
        if (!tasks.containsKey(task.getId())) {
            task.setId(nextId);
            tasks.put(nextId, task);
            return task;
        } else {
            Task taskToPut = new Task(
                    task.getName(),
                    task.getDescription(),
                    nextId,
                    task.getStatus()
            );
            tasks.put(taskToPut.getId(), taskToPut);
            return taskToPut;
        }
    }

    public Epic postEpic(Epic epic) {
        int nextId = getNextId();
        if (!epics.containsKey(epic.getId())) {
            epic.setId(nextId);
            epics.put(nextId, epic);
            return epics.put(epic.getId(), epic);
        } else {
            Epic epicToPut = new Epic(
                    epic.getName(),
                    epic.getDescription()
            );
            epicToPut.setId(nextId);
            epics.put(epicToPut.getId(), epicToPut);
            return epicToPut;
        }
    }

    public SubTask postSubTask(SubTask subTask) {
        int nextId = getNextId();
        if (!subtasks.containsKey(subTask.getId())) {
            subTask.setId(nextId);
            epics.get(subTask.getEpic().getId()).putSubTaskToEpic(subTask);
            subtasks.put(nextId, subTask);
            return subTask;
        } else {
            SubTask subTaskToPut = new SubTask(
                    subTask.getName(),
                    subTask.getDescription(),
                    subTask.getEpic()
            );
            subTaskToPut.setId(nextId);
            epics.get(subTask.getEpic().getId()).putSubTaskToEpic(subTask);
            subtasks.put(subTaskToPut.getId(), subTaskToPut);
            return subTaskToPut;
        }
    }

    public Task updateTask(Task task) {
        if (tasks.containsKey(task.getId())) {
            tasks.get(task.getId()).setName(task.getName());
            tasks.get(task.getId()).setDescription(task.getDescription());
            tasks.get(task.getId()).setStatus(task.getStatus());
        }
        return task;
    }

    public Epic updateEpic(Epic epic) {
        if (epics.containsKey(epic.getId())) {
            epics.get(epic.getId()).setName(epic.getName());
            epics.get(epic.getId()).setDescription(epic.getDescription());
            epics.get(epic.getId()).setStatus(epic.getStatus());
        }
        return epic;
    }

    public SubTask updateSubTask(SubTask subTask) {
        if (subtasks.containsKey(subTask.getId())) {
            subtasks.get(subTask.getId()).setName(subTask.getName());
            subtasks.get(subTask.getId()).setDescription(subTask.getDescription());
            subtasks.get(subTask.getId()).setStatus(subTask.getStatus());
        }
        return subTask;
    }

    public Task deleteTaskById(int id) {
        return tasks.remove(id);
    }

    public Epic deleteEpicById(int id) {
        if (epics.get(id) != null) {
            ArrayList<SubTask> subTasksList = epics.get(id).getSubTaskArrayList();
            if (!subTasksList.isEmpty()) {
                subTasksList.clear();
            }
            if (epics.get(id).getSubTaskArrayList().isEmpty()) {
                return epics.remove(id);
            } else {
                throw new RuntimeException("Подзадачи эпика не были удалены.");
            }
        } else {
            return null;
        }
    }

    public SubTask deleteSubTaskById(int id) {
        if (subtasks.get(id) != null) {
            return subtasks.remove(id);
        } else {
            return null;
        }
    }

    public int getNextId() {
        return id++;
    }

    public ArrayList<SubTask> getSubTasksFromEpic(Epic epic) {
        return epic.getSubTaskArrayList();
    }

}