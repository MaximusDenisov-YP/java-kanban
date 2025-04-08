package ru.kanban;

import ru.kanban.entity.Epic;
import ru.kanban.entity.Subtask;
import ru.kanban.entity.Task;
import ru.kanban.entity.TaskStatus;
import ru.kanban.manager.HistoryManager;
import ru.kanban.manager.Managers;
import ru.kanban.manager.TaskManager;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

public class Main {
    // Доп. задание - пользовательский сценарий.
    public static void main(String[] args) {
        TaskManager taskManager = Managers.getDefault();
        HistoryManager historyManager = taskManager.getHistoryManager();
        createNeededTasks(taskManager);
        getAllTasksById(taskManager);
        System.out.println("\nПросмотрели все таски\n");
        printHistory(historyManager);
        int beforeRandomCount = historyManager.getHistory().size();
        getRandomTasksById(taskManager);
        System.out.println("\nПросмотрели рандомно все таски\n");
        printHistory(historyManager);
        int afterRandomCount = historyManager.getHistory().size();
        assertEquals(beforeRandomCount, afterRandomCount, "После рандомного просмотра всех тасок, изменилось кол-во!");
        taskManager.deleteEpicById(2);
        System.out.println("\nУдалили наполненный Epic\n");
        assertEquals(3, historyManager.getHistory().size());
        printHistory(historyManager);
    }

    private static void printHistory(HistoryManager historyManager) {
        for (Task task : historyManager.getHistory()) {
            System.out.println(task);
        }
    }

    private static void createNeededTasks(TaskManager taskManager) {
        // Создайте две задачи, эпик с тремя подзадачами и эпик без подзадач.
        Task someTask = getTestTask();
        taskManager.postTask(someTask);
        Task someTask1 = getTestTask();
        taskManager.postTask(someTask1);

        Epic epicWithSubtask = getTestEpic();
        taskManager.postEpic(epicWithSubtask);
        Subtask someSubtask = getTestSubtask(taskManager.getEpicById(2));
        taskManager.postSubtask(someSubtask);
        Subtask someSubtask1 = getTestSubtask(taskManager.getEpicById(2));
        taskManager.postSubtask(someSubtask1);
        Subtask someSubtask2 = getTestSubtask(taskManager.getEpicById(2));
        taskManager.postSubtask(someSubtask2);

        Epic epicWithoutSubtask = getTestEpic();
        taskManager.postEpic(epicWithoutSubtask);
    }

    private static void getAllTasksById(TaskManager taskManager) {
        for (Task task : taskManager.getTasks()) {
            taskManager.getTaskById(task.getId());
        }
        for (Epic epic : taskManager.getEpics()) {
            taskManager.getEpicById(epic.getId());
        }
        for (Subtask subtask : taskManager.getSubtasks()) {
            taskManager.getSubtaskById(subtask.getId());
        }
    }

    private static void getRandomTasksById(TaskManager taskManager) {
        Random r = new Random();
        for (int i = 0; i <= 15; i++) {
            taskManager.getTaskById(r.nextInt(taskManager.getTasks().size() - 1));
        }
        for (int i = 0; i <= 15; i++) {
            taskManager.getSubtaskById(r.nextInt(taskManager.getSubtasks().size() - 1));
        }
        for (int i = 0; i <= 15; i++) {
            taskManager.getEpicById(r.nextInt(taskManager.getEpics().size() - 1));
        }
    }

    private static Task getTestTask() {
        return new Task("Разработка", "Разработать приложение \"Kanban-доска\"", TaskStatus.NEW);
    }

    private static Subtask getTestSubtask(Epic epic) {
        return new Subtask("Написание кода", "Написать код", epic);
    }

    private static Epic getTestEpic() {
        return new Epic("Тираж", "Тиражировать Kanban");
    }
}
