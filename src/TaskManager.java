import entity.Epic;
import entity.SubTask;
import entity.Task;
import entity.TaskStatus;

import java.util.ArrayList;
import java.util.HashMap;

public class TaskManager {
    private static int id = 0;

    private HashMap<Integer, Task> kanbanDesc;

    public TaskManager() {
        kanbanDesc = new HashMap<>();
    }

    public HashMap<Integer, Task> getAllTasks() {
        for (TaskStatus taskStatus : TaskStatus.values()) {
            printTasksWithStatus(taskStatus);
        }
        System.out.println("------------------------------------");
        return kanbanDesc;
    }

    public void deleteAllTasks() {
        kanbanDesc.clear();
        System.out.println("Все задачи удалены!");
    }

    public Task getTaskById(int id) {
        return kanbanDesc.get(id);
    }

    public Task postTask(Task task) {
        if (!kanbanDesc.containsKey(task.getId())) {
            if (task.getClass() == SubTask.class) {
                SubTask subTask = (SubTask) task;
                Epic epic = subTask.getEpic();
                epic.putSubTaskToEpic(subTask);
            }
            System.out.println("Добавлен объект типа " +
                    task.getClass().toString() +
                    " с названием " + task.getName() +
                    " и ID = " + task.getId());
            return kanbanDesc.put(task.getId(), task);
        } else {
            System.out.println("Задача с таким ID уже создана!");
            return null;
        }
    }

    public Task updateTask(Task task) {
        if (kanbanDesc.containsKey(task.getId())) {
            kanbanDesc.get(task.getId()).setName(task.getName());
            kanbanDesc.get(task.getId()).setDescription(task.getDescription());
            kanbanDesc.get(task.getId()).setStatus(task.getStatus());
        } else {
            System.err.println("Объекта с таким ID не существует!");
        }
        return task;
    }

    public Task deleteTaskById(int id) {
        return kanbanDesc.remove(id);
    }

    public int getNextId() {
        try {
            return id;
        } finally {
            id++;
        }
    }

    public ArrayList<SubTask> getSubTasksFromEpic(Task task) {
        if (task.getClass() == Epic.class) {
            Epic epic = (Epic) task;
            return epic.getSubTaskArrayList();
        } else {
            System.err.printf("Задача под названием %s - не является типом Epic", task.getName());
            return null;
        }
    }

    private void printTasksWithStatus(TaskStatus taskStatus) {
        System.out.println("------------------------------------");
        System.out.printf("Задачи в статусе: %s\n", taskStatus);
        for (Integer id : kanbanDesc.keySet()) {
            if (kanbanDesc.get(id).getStatus() == taskStatus) {
                System.out.println(kanbanDesc.get(id).toString());
            } else {
                System.out.println("В данном статусе задач нет!");
                return;
            }
        }
    }

}
