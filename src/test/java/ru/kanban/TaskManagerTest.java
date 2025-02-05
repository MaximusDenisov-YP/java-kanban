package test.java.ru.kanban;

import main.java.ru.kanban.manager.TaskManager;
import main.java.ru.kanban.entity.Epic;
import main.java.ru.kanban.entity.SubTask;
import main.java.ru.kanban.entity.Task;
import main.java.ru.kanban.entity.TaskStatus;

public class TaskManagerTest {

    public static void main(String[] args) {
        TaskManager taskManager = new TaskManager();
        getMockTasks(taskManager);
        System.out.println(taskManager.getTasks());
        System.out.println(taskManager.getEpics());
        System.out.println(taskManager.getSubtasks());
        Epic epic = taskManager.getEpicById(1);
        System.out.println("Отдельно выведенные SubTasks: \n" + taskManager.getSubTasksFromEpic(epic));
        testEpicStatusChange(epic);
        testUpdateTask(taskManager, epic);
        testDeleteTask(taskManager);
    }

    private static void testDeleteTask(TaskManager taskManager) {
        Task task = taskManager.getTaskById(0);
        if (task == null) throw new RuntimeException("Сравниваемый объект пуст!");
        taskManager.deleteTaskById(0);
        task = taskManager.getTaskById(0);
        if (task != null) throw new RuntimeException("Объект не был удалён");
        System.out.println("Тест DeleteById пройден успешно!");

        taskManager.deleteAllTasks();
        if (!taskManager.getTasks().isEmpty())
            throw new RuntimeException("Список задач не пуст! Удаление не произошло!");
        System.out.println("Тест Delete Tasks пройден успешно!");

        taskManager.deleteAllEpics();
        if (!taskManager.getTasks().isEmpty())
            throw new RuntimeException("Список эпик задач не пуст! Удаление не произошло!");
        System.out.println("Тест Delete Epics пройден успешно!");

        if (!taskManager.getTasks().isEmpty())
            throw new RuntimeException("Список подзадач не пуст! Удаление не произошло!");
        System.out.println("Тест Delete SubTasks пройден успешно!");

    }

    private static void testUpdateTask(TaskManager taskManager, Epic epic) {
        if (!epic.getName().equals("Тираж"))
            throw new RuntimeException("Имя задачи изначально некорректное");

        if (!epic.getDescription().equals("Тиражировать Kanban"))
            throw new RuntimeException("Описание задачи изначально некорректное");

        taskManager.updateEpic(
                new Epic(
                        "Тираж доски Kanban",
                        "Тиражировать Kanban до начала 5-го спринта",
                        1
                )
        );
        if (!epic.getName().equals("Тираж доски Kanban"))
            throw new RuntimeException("Имя задачи не изменилось");

        if (!epic.getDescription().equals("Тиражировать Kanban до начала 5-го спринта"))
            throw new RuntimeException("Описание задачи не изменилось");

        System.out.println("Тесты Update прошли успешно!");
    }

    private static void testEpicStatusChange(Epic epic) {
        // Изначально все 3 SubTasks NEW
        if (epic != null) {
            epic.checkEpicStatus();
            if (!epic.getStatus().toString().equals("NEW")) throw new RuntimeException("Итоговое значение не NEW");
            // Все 3 SubTasks DONE
            for (SubTask subTask : epic.getSubTaskArrayList()) {
                subTask.setStatus(TaskStatus.DONE);
            }
            epic.checkEpicStatus();
            if (!epic.getStatus().toString().equals("DONE")) throw new RuntimeException("Итоговое значение не DONE");

            // Меняем значение статуса одного из SubTask на NEW
            SubTask subTask1 = epic.getSubTaskArrayList().get(2);
            subTask1.setStatus(TaskStatus.NEW);
            epic.checkEpicStatus();
            if (!epic.getStatus().toString().equals("IN_PROGRESS"))
                throw new RuntimeException("Итоговое значение не IN_PROGRESS");

            // Меняем значение статуса одного из SubTask на IN_PROGRESS
            subTask1.setStatus(TaskStatus.IN_PROGRESS);
            if (!epic.getStatus().toString().equals("IN_PROGRESS"))
                throw new RuntimeException("Итоговое значение не IN_PROGRESS");

            System.out.println("Тесты Epic прошли успешно!");
        } else {
            throw new RuntimeException("Epic пуст!");
        }
    }

    private static TaskManager getMockTasks(TaskManager taskManager) {
        taskManager.postTask(new Task("Разработка", "Разработать приложение \"Kanban-доска\"", TaskStatus.NEW));
        Epic epic = new Epic("Тираж", "Тиражировать Kanban");
        taskManager.postEpic(epic);
        taskManager.postSubTask(new SubTask("Тех требования", "Написать технические требования", epic));
        taskManager.postSubTask(new SubTask("Написание кода", "Написать код", epic));
        taskManager.postSubTask(new SubTask("Тестирование кода", "Тестировать код", epic));
        return taskManager;
    }
}
