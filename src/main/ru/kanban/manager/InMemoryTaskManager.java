package ru.kanban.manager;

import ru.kanban.entity.Epic;
import ru.kanban.entity.Subtask;
import ru.kanban.entity.Task;
import ru.kanban.exception.ManagerSaveException;

import java.time.LocalDateTime;
import java.util.*;

public class InMemoryTaskManager implements TaskManager {

    private int id = 0;

    private final Map<Integer, Task> tasks = new HashMap<>();
    private final Map<Integer, Epic> epics = new HashMap<>();
    private final Map<Integer, Subtask> subtasks = new HashMap<>();

    private final HistoryManager historyManager = Managers.getDefaultHistory();

    private final TreeSet<Task> prioritizedTasks = new TreeSet<>(Comparator.comparing(Task::getStartTime));

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
    public List<Task> getPrioritizedTasks() {
        return new ArrayList<>(prioritizedTasks);
    }

    @Override
    public Optional<Task> getTaskById(int id) {
        Optional<Task> result = Optional.ofNullable(tasks.get(id));
        result.ifPresent(historyManager::add);
        return result;
    }

    @Override
    public Optional<Epic> getEpicById(int id) {
        Optional<Epic> result = Optional.ofNullable(epics.get(id));
        result.ifPresent(historyManager::add);
        return result;
    }

    @Override
    public Optional<Subtask> getSubtaskById(int id) {
        Optional<Subtask> result = Optional.ofNullable(subtasks.get(id));
        result.ifPresent(historyManager::add);
        return result;
    }

    public int getNextId() {
        return id++;
    }

    public ArrayList<Subtask> getSubtasksFromEpic(Epic epic) {
        return epic.getSubtaskArrayList();
    }

    @Override
    public Task postTask(Task task) {
        if (task.getStartTime() != null && task.getDuration() != null) {
            checkTimeCrossing(task);
            prioritizedTasks.add(task);
        }
        int nextId = getNextId();
        if (isContainsTaskId(task)) {
            Task taskToPut = new Task(task);
            tasks.put(taskToPut.getId(), taskToPut);
            return taskToPut;
        } else {
            Task taskToPut = new Task(task);
            taskToPut.setId(nextId);
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
            Epic epicToPut = new Epic(epic);
            epicToPut.setId(nextId);
            return epics.put(nextId, epicToPut);
        }
    }

    @Override
    public Subtask postSubtask(Subtask subTask) {
        if (subTask.getStartTime() != null && subTask.getDuration() != null) {
            checkTimeCrossing(subTask);
        }
        int nextId = getNextId();
        if (isContainsTaskId(subTask)) {
            Subtask subtaskToPut = new Subtask(subTask);
            Epic epic = epics.get(subtaskToPut.getEpic().getId());
            epic.putSubtaskToEpic(subtaskToPut);
            epic.changeStartTimeAndDuration();
            prioritizedTasks.add(subtaskToPut);
            return subtasks.put(subTask.getId(), subtaskToPut);
        } else {
            Subtask subTaskToPut = new Subtask(subTask);
            subTaskToPut.setId(nextId);
            Epic subtasksEpic = subTaskToPut.getEpic();
            subtasksEpic.putSubtaskToEpic(subTaskToPut);
            subtasksEpic.changeStartTimeAndDuration();
            prioritizedTasks.add(subTaskToPut);
            return subtasks.put(nextId, subTaskToPut);
        }
    }

    @Override
    public Task updateTask(Task task) {
        if (tasks.containsKey(task.getId())) {
            prioritizedTasks.remove(tasks.get(task.getId()));
            tasks.get(task.getId()).setName(task.getName());
            tasks.get(task.getId()).setDescription(task.getDescription());
            tasks.get(task.getId()).setStatus(task.getStatus());
            prioritizedTasks.add(tasks.get(task.getId()));
        }
        return task;
    }

    @Override
    public Epic updateEpic(Epic epic) {
        if (epics.containsKey(epic.getId())) {
            epics.get(epic.getId()).setName(epic.getName());
            epics.get(epic.getId()).setDescription(epic.getDescription());
        }
        return epic;
    }

    @Override
    public Subtask updateSubtask(Subtask subTask) {
        if (subtasks.containsKey(subTask.getId())) {
            prioritizedTasks.remove(subtasks.get(subTask.getId()));
            subtasks.get(subTask.getId()).setName(subTask.getName());
            subtasks.get(subTask.getId()).setDescription(subTask.getDescription());
            subtasks.get(subTask.getId()).setStatus(subTask.getStatus());
            prioritizedTasks.add(subtasks.get(subTask.getId()));
        }
        subTask.getEpic().checkEpicStatus();
        return subTask;
    }


    @Override
    public void deleteAllTasks() {
        tasks.values().forEach(task -> historyManager.remove(task.getId()));
        prioritizedTasks.removeAll(tasks.values());
        tasks.clear();
    }

    @Override
    public void deleteAllSubtasks() {
        epics.values().forEach(epic -> {
            epic.clearSubtasks();
            epic.checkEpicStatus();
        });
        subtasks.values().forEach(subtask -> historyManager.remove(subtask.getId()));
        prioritizedTasks.removeAll(subtasks.values());
        subtasks.clear();
    }

    @Override
    public void deleteAllEpics() {
        epics.values().forEach(epic -> {
            ArrayList<Subtask> subTasksList = epics.get(epic.getId()).getSubtaskArrayList();

            if (subTasksList != null) {
                if (!subTasksList.isEmpty()) {
                    subTasksList.forEach(subtask -> historyManager.remove(subtask.getId()));
                    subTasksList.clear();
                }
            }
            historyManager.remove(epic.getId());
        });
        epics.clear();
        prioritizedTasks.removeAll(subtasks.values());
        subtasks.clear();
    }

    @Override
    public Task deleteTaskById(int id) {
        historyManager.remove(id);
        prioritizedTasks.remove(tasks.get(id));
        return tasks.remove(id);
    }

    @Override
    public Epic deleteEpicById(int id) {
        if (epics.get(id) != null) {
            ArrayList<Subtask> subTasksList = epics.get(id).getSubtaskArrayList();
            if (!subTasksList.isEmpty()) {
                subTasksList.forEach(subtask -> {
                    historyManager.remove(subtask.getId());
                    subtasks.remove(subtask.getId());
                    prioritizedTasks.remove(subtask);
                });
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
        Subtask subtaskToDelete = subtasks.get(id);
        if (subtaskToDelete != null) {
            Epic subtasksEpic = subtaskToDelete.getEpic();
            subtasksEpic.getSubtaskArrayList().remove(subtasks.get(id));
            subtasksEpic.changeStartTimeAndDuration();
            subtasksEpic.checkEpicStatus();
            historyManager.remove(id);
            prioritizedTasks.remove(subtaskToDelete);
            return subtasks.remove(id);
        } else {
            return null;
        }
    }

    @Override
    public boolean isTimeCrossing(Task newTask) {
        if (newTask.getStartTime() == null || newTask.getDuration() == null) {
            return false; // Новая задача без времени — нет смысла проверять
        }
        Task lower = prioritizedTasks.lower(newTask);
        Task higher = prioritizedTasks.higher(newTask);
        LocalDateTime newStart = newTask.getStartTime();
        LocalDateTime newEnd = newTask.getEndTime();
        if (lower != null
                && lower.getStartTime() != null
                && lower.getDuration() != null) {
            LocalDateTime lowerEnd = lower.getEndTime();
            return newStart.isBefore(lowerEnd) && lower.getStartTime().isBefore(newEnd);
        }
        if (higher != null
                && higher.getStartTime() != null
                && higher.getDuration() != null) {
            LocalDateTime higherEnd = higher.getEndTime();
            return newStart.isBefore(higherEnd) && higher.getStartTime().isBefore(newEnd);
        }
        return false;
    }

    private void checkTimeCrossing(Task task) {
        if (isTimeCrossing(task)) throw new ManagerSaveException(
                String.format(
                        "Новая задача пересекается по времени с уже существующей!\nStartTime = %s\nEndTime = %s",
                        task.getStartTime(),
                        task.getEndTime()
                )
        );
    }

    private boolean isContainsTaskId(Task task) {
        return !tasks.containsKey(task.getId()) && !epics.containsKey(task.getId()) && !subtasks.containsKey(task.getId());
    }
}