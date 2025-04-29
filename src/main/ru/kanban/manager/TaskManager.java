package ru.kanban.manager;

import ru.kanban.entity.Epic;
import ru.kanban.entity.Subtask;
import ru.kanban.entity.Task;

import java.util.ArrayList;
import java.util.Optional;
import java.util.TreeSet;

public interface TaskManager {

    ArrayList<Task> getTasks();

    ArrayList<Subtask> getSubtasks();

    ArrayList<Epic> getEpics();

    void deleteAllTasks();

    void deleteAllSubtasks();

    void deleteAllEpics();

    Optional<Task> getTaskById(int id);

    Optional<Epic> getEpicById(int id);

    Optional<Subtask> getSubtaskById(int id);

    Task postTask(Task task);

    Epic postEpic(Epic epic);

    Subtask postSubtask(Subtask subTask);

    Task updateTask(Task task);

    Epic updateEpic(Epic epic);

    Subtask updateSubtask(Subtask subTask);

    Task deleteTaskById(int id);

    Epic deleteEpicById(int id);

    Subtask deleteSubtaskById(int id);

    int getNextId();

    ArrayList<Subtask> getSubtasksFromEpic(Epic epic);

    HistoryManager getHistoryManager();

    TreeSet<Task> getPrioritizedTasks();

    boolean isTimeCrossing(Task task);

}