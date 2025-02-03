import entity.Epic;
import entity.SubTask;
import entity.Task;
import entity.TaskStatus;

public class TaskManagerTest {

    public static void main(String[] args) {
        TaskManager taskManager = new TaskManager();
        getMockTasks(taskManager);
        Epic epic = (Epic) taskManager.getTaskById(1);

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
        System.out.println("Тест Delete пройден успешно!");
        taskManager.deleteAllTasks();
        if (!taskManager.getAllTasks().isEmpty())
            throw new RuntimeException("Список задач не пуст! Удаление не произошло!");
        System.out.println("Тест All Delete пройден успешно!");
    }

    private static void testUpdateTask(TaskManager taskManager, Epic epic) {
        if (!epic.getName()
                .equals("Тираж")
        ) throw new RuntimeException("Имя задачи изначально некорректное");

        if (!epic.getDescription()
                .equals("Тиражировать Kanban")
        ) throw new RuntimeException("Описание задачи изначально некорректное");

        taskManager.updateTask(
                new Epic(
                        "Тираж доски Kanban",
                        "Тиражировать Kanban до начала 5-го спринта",
                        1
                )
        );
        if (!epic.getName()
                .equals("Тираж доски Kanban")
        ) throw new RuntimeException("Имя задачи не изменилось");

        if (!epic.getDescription()
                .equals("Тиражировать Kanban до начала 5-го спринта")
        ) throw new RuntimeException("Описание задачи не изменилось");

        System.out.println("Тесты Update прошли успешно!");
    }

    private static void testEpicStatusChange(Epic epic) {
        // Изначально все 3 SubTasks NEW
        if (!epic.checkStatus().toString().equals("NEW")) throw new RuntimeException("Итоговое значение не NEW");
        // Все 3 SubTasks DONE
        for (SubTask subTask : epic.getSubTaskArrayList()) {
            subTask.setStatus(TaskStatus.DONE);
        }
        if (!epic.checkStatus().toString().equals("DONE")) throw new RuntimeException("Итоговое значение не DONE");
        // Меняем значение статуса одного из SubTask на NEW
        SubTask subTask1 = epic.getSubTaskArrayList().get(2);
        subTask1.setStatus(TaskStatus.NEW);
        if (!epic.checkStatus().toString().equals("IN_PROGRESS"))
            throw new RuntimeException("Итоговое значение не IN_PROGRESS");
        // Меняем значение статуса одного из SubTask на IN_PROGRESS
        subTask1.setStatus(TaskStatus.IN_PROGRESS);
        if (!epic.checkStatus().toString().equals("IN_PROGRESS"))
            throw new RuntimeException("Итоговое значение не IN_PROGRESS");

        System.out.println("Тесты Epic прошли успешно!");
    }

    private static TaskManager getMockTasks(TaskManager taskManager) {
        taskManager.postTask(
                new Task(
                        "Разработка",
                        "Разработать приложение \"Kanban-доска\"",
                        taskManager.getNextId(),
                        TaskStatus.NEW
                )
        );
        taskManager.postTask(
                new Epic(
                        "Тираж",
                        "Тиражировать Kanban",
                        taskManager.getNextId()
                )
        );
        taskManager.postTask(
                new SubTask(
                        "Тех требования",
                        "Написать технические требования",
                        taskManager.getNextId(),
                        (Epic) taskManager.getTaskById(1))
        );
        taskManager.postTask(
                new SubTask(
                        "Написание кода",
                        "Написать код",
                        taskManager.getNextId(),
                        (Epic) taskManager.getTaskById(1)
                )
        );
        taskManager.postTask(
                new SubTask(
                        "Тестирование кода",
                        "Тестировать код",
                        taskManager.getNextId(),
                        (Epic) taskManager.getTaskById(1)
                )
        );
        taskManager.getAllTasks();
        return taskManager;
    }
}
