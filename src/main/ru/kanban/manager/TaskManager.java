package ru.kanban.manager;

import ru.kanban.entity.Epic;
import ru.kanban.entity.Subtask;
import ru.kanban.entity.Task;

import java.util.ArrayList;

public interface TaskManager {

    ArrayList<Task> getTasks();

    ArrayList<Subtask> getSubtasks();

    ArrayList<Epic> getEpics();

    void deleteAllTasks();

    void deleteAllSubtasks();

    void deleteAllEpics();

    Task getTaskById(int id);

    Epic getEpicById(int id);

    Subtask getSubtaskById(int id);

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

}