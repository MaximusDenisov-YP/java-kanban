import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.*;
import ru.kanban.entity.Epic;
import ru.kanban.entity.Subtask;
import ru.kanban.entity.Task;
import ru.kanban.entity.TaskStatus;
import ru.kanban.manager.HistoryManager;
import ru.kanban.manager.Managers;
import ru.kanban.manager.TaskManager;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

public class TaskManagerTest {

    private TaskManager taskManager;
    private HistoryManager historyManager;

    @BeforeEach
    void beforeEach() {
        taskManager = Managers.getDefault();
        historyManager = taskManager.getHistoryManager();
    }

    @Test
    @DisplayName("Проверка корректного создания Task")
    void addCorrectTaskToTaskList() {
        Task task = getTestTask();
        taskManager.postTask(task);
        assertFalse(taskManager.getTasks().isEmpty());
        assertEquals(task.getName(), taskManager.getTaskById(0).getName());
        assertEquals(task.getStatus(), taskManager.getTaskById(0).getStatus());
        assertEquals(task.getDescription(), taskManager.getTaskById(0).getDescription());
    }

    @Test
    @DisplayName("Проверка корректного создания Epic")
    void addCorrectEpicToEpicList() {
        Epic epic = getTestEpic();
        taskManager.postEpic(epic);
        assertFalse(taskManager.getEpics().isEmpty());
        assertEquals(epic.getName(), taskManager.getEpicById(0).getName());
        assertEquals(epic.getDescription(), taskManager.getEpicById(0).getDescription());
        assertEquals(epic.getStatus(), taskManager.getEpicById(0).getStatus());
    }

    @Test
    @DisplayName("Проверка корректного создания Subtask")
    void addCorrectSubtaskToEpicList() {
        Epic epic = getTestEpic();
        Subtask subtask = getTestSubtask(epic);
        taskManager.postEpic(epic);
        taskManager.postSubtask(subtask);
        assertFalse(taskManager.getSubtasks().isEmpty());
        assertEquals(subtask.getEpic(), taskManager.getSubtaskById(1).getEpic());
        assertEquals(subtask.getDescription(), taskManager.getSubtaskById(1).getDescription());
        assertEquals(subtask.getName(), taskManager.getSubtaskById(1).getName());
    }

    @Test
    @DisplayName("Проверка корректного удаления Task по ID")
    void deleteCorrectTaskByIdInTaskList() {
        Task task = getTestTask();
        taskManager.postTask(task);
        assertFalse(taskManager.getTasks().isEmpty());
        taskManager.deleteTaskById(0);
        assertTrue(taskManager.getTasks().isEmpty());
    }

    @Test
    @DisplayName("Проверка корректного удаления Epic с Subtasks по ID")
    void deleteCorrectEpicByIdInEpicList() {
        Epic epic = getTestEpic();
        taskManager.postEpic(epic);
        Subtask someSubtask = getTestSubtask(epic);
        Subtask someSubtask1 = getTestSubtask(epic);
        taskManager.postSubtask(someSubtask);
        taskManager.postSubtask(someSubtask1);
        assertFalse(taskManager.getSubtasks().isEmpty());
        assertFalse(taskManager.getEpics().isEmpty());
        taskManager.deleteEpicById(0);
        assertTrue(taskManager.getEpics().isEmpty());
        assertFalse(taskManager.getSubtasks().contains(someSubtask));
        assertFalse(taskManager.getSubtasks().contains(someSubtask1));
    }

    @Test
    @DisplayName("Проверка корректного удаления Subtask по ID")
    void deleteCorrectSubtaskByIdInSubtaskList() {
        Epic epic = getTestEpic();
        Subtask subtask = getTestSubtask(epic);
        taskManager.postEpic(epic);
        taskManager.postSubtask(subtask);
        assertFalse(taskManager.getSubtasks().isEmpty());
        taskManager.deleteSubtaskById(1);
        assertTrue(taskManager.getSubtasks().isEmpty());
    }

    @Test
    @DisplayName("Проверка корректного удаления всех Task")
    void deleteCorrectAllTaskInTaskList() {
        for (int i = 0; i < 4; i++) {
            taskManager.postTask(getTestTask());
        }
        assertTrue(taskManager.getTasks().size() > 1);
        taskManager.deleteAllTasks();
        assertTrue(taskManager.getTasks().isEmpty());
    }

    @Test
    @DisplayName("Проверка корректного удаления всех Task из History")
    void deleteCorrectTasksInHistoryList() {
        Task someTask = getTestTask();
        Task someTask1 = getTestTask();
        Epic someEpic = getTestEpic();
        Epic someEpic1 = getTestEpic();
        Subtask someSubtask = getTestSubtask(someEpic);
        Subtask someSubtask1 = getTestSubtask(someEpic);
        Subtask someSubtask2 = getTestSubtask(someEpic);

        taskManager.postTask(someTask);
        taskManager.postTask(someTask1);
        taskManager.postEpic(someEpic);
        taskManager.postEpic(someEpic1);
        taskManager.postSubtask(someSubtask);
        taskManager.postSubtask(someSubtask1);
        taskManager.postSubtask(someSubtask2);

        for (Task task : taskManager.getTasks()) {
            taskManager.getTaskById(task.getId());
        }
        for (Task task : taskManager.getTasks()) {
            taskManager.getTaskById(task.getId());
        }
        for (Epic epic : taskManager.getEpics()) {
            taskManager.getEpicById(epic.getId());
        }
        for (Subtask subtask : taskManager.getSubtasks()) {
            taskManager.getSubtaskById(subtask.getId());
        }
        for (Task task : taskManager.getTasks()) {
            taskManager.getTaskById(task.getId());
        }
        for (Epic epic : taskManager.getEpics()) {
            taskManager.getEpicById(epic.getId());
        }

        System.out.println(historyManager.getHistory());

        historyManager.remove(4);
        System.out.println("\n");
        System.out.println(historyManager.getHistory());

        historyManager.remove(1);
        System.out.println("\n");
        System.out.println(historyManager.getHistory());

        historyManager.remove(3);
        System.out.println("\n");
        System.out.println(historyManager.getHistory());

        historyManager.remove(0);
        historyManager.remove(2);
        historyManager.remove(6);
        System.out.println("\n");
        historyManager.getHistory();
        for (Task task : taskManager.getTasks()) {
            taskManager.getTaskById(task.getId());
        }
        for (Epic epic : taskManager.getEpics()) {
            taskManager.getEpicById(epic.getId());
        }
        System.out.println("\n");
        historyManager.getHistory();
    }

    @Test
    @DisplayName("Проверка корректного удаления всех Epic")
    void deleteCorrectAllEpicInEpicList() {
        for (int i = 0; i < 4; i++) {
            taskManager.postEpic(getTestEpic());
        }
        assertTrue(taskManager.getEpics().size() > 1);
        taskManager.deleteAllEpics();
        assertTrue(taskManager.getEpics().isEmpty());
    }

    @Test
    @DisplayName("Проверка корректного удаления всех Subtask")
    void deleteCorrectAllSubtaskInSubtaskList() {
        Epic epic = getTestEpic();
        taskManager.postEpic(epic);
        for (int i = 0; i < 4; i++) {
            taskManager.postSubtask(getTestSubtask(epic));
        }
        assertTrue(taskManager.getSubtasks().size() > 1);
        taskManager.deleteAllSubtasks();
        assertTrue(taskManager.getSubtasks().isEmpty());
    }

    @Test
    @DisplayName("Проверка корректного обновление Task")
    void updateCorrectTaskInTaskList() {
        Task task = getTestTask();
        taskManager.postTask(task);
        assertEquals(task, taskManager.getTaskById(0));
        Task taskToChange = new Task("a", "b", TaskStatus.IN_PROGRESS);
        taskToChange.setId(0);
        taskManager.updateTask(taskToChange);
        assertEquals(taskToChange, taskManager.getTaskById(0));
    }

    @Test
    @DisplayName("Проверка корректного обновления Epic")
    void updateCorrectEpicInEpicList() {
        Epic epic = getTestEpic();
        taskManager.postEpic(epic);
        assertEquals(epic, taskManager.getEpicById(0));
        Epic epicToChange = new Epic("a", "b");
        epicToChange.setId(0);
        taskManager.updateEpic(epicToChange);
        assertEquals(epicToChange, taskManager.getEpicById(0));
    }

    @Test
    @DisplayName("Проверка корректного обновления Subtask")
    void updateCorrectSubtaskInSubtaskList() {
        Epic epic = getTestEpic();
        taskManager.postEpic(epic);
        Subtask subtask = getTestSubtask(epic);
        taskManager.postSubtask(subtask);
        assertEquals(subtask.getName(), taskManager.getSubtaskById(1).getName());
        assertEquals(subtask.getDescription(), taskManager.getSubtaskById(1).getDescription());
        assertEquals(subtask.getStatus(), taskManager.getSubtaskById(1).getStatus());
        Subtask subtaskToChange = new Subtask("a", "b", epic, LocalDateTime.now(), Duration.ofMinutes(180));
        subtaskToChange.setId(1);
        taskManager.updateSubtask(subtaskToChange);
        assertEquals(subtaskToChange, taskManager.getSubtaskById(1));
    }

    @Test
    @DisplayName("Проверка получения списка SubTasks из Epic")
    void getCorrectListSubTasksOfEpic() {
        Epic epic = getTestEpic();
        taskManager.postEpic(epic);
        Subtask subtask = getTestSubtask(epic);
        Subtask subtask1 = getTestSubtask(epic);
        taskManager.postSubtask(subtask);
        taskManager.postSubtask(subtask1);
        assertTrue(taskManager.getSubtasksFromEpic(epic).size() > 1);
    }

    @Test
    @DisplayName("Проверка равенства задач с одинаковым ID")
    void comparePositiveTaskWithTaskById() {
        Task firstTask = getTestTask();
        Task secondTask = getTestTask();
        secondTask.setName("AbsolutelyOtherName");
        secondTask.setDescription("AbsolutelyOtherDescription");
        assertEquals(firstTask, secondTask);
    }

    @Test
    @DisplayName("Проверка равенства задач разного типа")
    void comparePositiveTaskWithAnotherClassTaskById() {
        Task firstTask = getTestTask();
        Epic secondTask = getTestEpic();
        Subtask thirdTask = getTestSubtask(secondTask);
        secondTask.setId(firstTask.getId());
        thirdTask.setId(firstTask.getId());
        // Проверяем, что любые типы наследуемые от Task компарируются между собой положительно
        assertEquals(firstTask, secondTask);
        assertEquals(secondTask, thirdTask);
        assertEquals(firstTask, thirdTask);
    }

    @Test
    @DisplayName("InMemoryTaskManager добавляет задачи разных типов и находит их по ID")
    void inMemoryTaskManagerAddsAndFindsTasksById() {
        Task task = getTestTask();
        task.setId(123);
        Epic epic = getTestEpic();
        epic.setId(111);
        Subtask subtask = getTestSubtask(epic);
        subtask.setId(999);

        taskManager.postTask(task);
        taskManager.postEpic(epic);
        taskManager.postSubtask(subtask);

        assertEquals(task.getName(), taskManager.getTaskById(123).getName());
        assertEquals(task.getDescription(), taskManager.getTaskById(123).getDescription());
        assertEquals(task.getStatus(), taskManager.getTaskById(123).getStatus());

        assertEquals(epic.getName(), taskManager.getEpicById(111).getName());
        assertEquals(epic.getDescription(), taskManager.getEpicById(111).getDescription());
        assertEquals(epic.getStatus(), taskManager.getEpicById(111).getStatus());

        assertEquals(subtask.getName(), taskManager.getSubtaskById(999).getName());
        assertEquals(subtask.getDescription(), taskManager.getSubtaskById(999).getDescription());
        assertEquals(subtask.getStatus(), taskManager.getSubtaskById(999).getStatus());
    }

    @Test
    @DisplayName("Задачи с заданным id и сгенерированным id не конфликтуют в менеджере")
    void manuallyAssignedIdDoesNotConflictWithGeneratedId() {
        Task manualTask = new Task("Manual Task", "Assigned ID", TaskStatus.NEW);
        manualTask.setId(100);
        taskManager.postTask(manualTask);

        Task autoTask = getTestTask();
        taskManager.postTask(autoTask);

        assertNotEquals(taskManager.getTaskById(manualTask.getId()), taskManager.getTaskById(autoTask.getId()));
    }

    @Test
    @DisplayName("Проверка неизменности задачи при добавлении в менеджер")
    void taskRemainsUnchangedAfterAddingToManager() {
        Task originalTask = getTestTask();
        originalTask.setId(999);
        taskManager.postTask(originalTask);

        Task retrievedTask = taskManager.getTaskById(originalTask.getId());

        assertEquals(originalTask.getName(), retrievedTask.getName());
        assertEquals(originalTask.getDescription(), retrievedTask.getDescription());
        assertEquals(originalTask.getStatus(), retrievedTask.getStatus());
        assertEquals(originalTask.getId(), retrievedTask.getId());
    }

    @Test
    @DisplayName("История сохраняет предыдущие версии задач")
    void historyManagerSavesPreviousVersionsOfTasks() {
        // Опустошаем менеджер историй

        Task task = getTestTask();
        taskManager.postTask(task);
        taskManager.getTaskById(task.getId());

        task.setName("Updated Name");
        task.setDescription("Updated Description");
        task.setStatus(TaskStatus.IN_PROGRESS);

        List<Task> history = historyManager.getHistory();
        assertEquals(1, history.size(), "Должна быть одна запись в истории");

        Task savedTask = history.get(0);
        assertNotEquals(task.getName(), savedTask.getName(), "История должна содержать предыдущую версию задачи");
        assertNotEquals(task.getDescription(), savedTask.getDescription(), "История должна содержать предыдущую версию задачи");
        assertNotEquals(task.getStatus(), savedTask.getStatus(), "История должна содержать предыдущую версию задачи");
        assertEquals("Разработка", savedTask.getName(), "Имя должно остаться прежним");
        assertEquals("Разработать приложение \"Kanban-доска\"", savedTask.getDescription(), "Описание должно остаться прежним");
        assertEquals(TaskStatus.NEW, savedTask.getStatus(), "Статус должен остаться прежним");
    }

    @Test
    @DisplayName("Проверка всеобъемливающего удаления Subtask")
    public void checkDeleteSubtasksInAllListAndEpicSubtaskList() {
        Epic epic = getTestEpic();
        taskManager.postEpic(epic);
        Epic epicInMemory = taskManager.getEpicById(0);
        taskManager.postSubtask(getTestSubtask(epicInMemory));
        taskManager.postSubtask(new Subtask(
                "Name",
                "Description",
                epicInMemory,
                LocalDateTime.now(),
                Duration.ofMinutes(180)
        ));
        taskManager.postSubtask(new Subtask(
                "SomeName",
                "SomeDescription",
                epicInMemory,
                LocalDateTime.now().plusDays(3),
                Duration.ofMinutes(1480)
        ));
        assertEquals(taskManager.getSubtasks().size(), 3);
        assertEquals(epicInMemory.getSubtaskArrayList().size(), 3);

        taskManager.deleteSubtaskById(3);
        assertEquals(taskManager.getSubtasks().size(), 2);
        assertEquals(epicInMemory.getSubtaskArrayList().size(), 2);

        taskManager.deleteAllSubtasks();
        assertEquals(taskManager.getSubtasks().size(), 0);
        assertEquals(epicInMemory.getSubtaskArrayList().size(), 0);
    }

    @Test
    @DisplayName("Проверка изменения статуса Epic при обновлении и удалении у него Subtask")
    public void checkStatusEpicWithUpdateAndDeleteHimSubtasks() {
        Epic epic = getTestEpic();
        taskManager.postEpic(epic);
        Epic epicInMemory = taskManager.getEpicById(0);
        taskManager.postSubtask(getTestSubtask(epicInMemory));
        taskManager.postSubtask(new Subtask(
                "Name",
                "Description",
                epicInMemory,
                LocalDateTime.now(),
                Duration.ofMinutes(300)
        ));
        taskManager.postSubtask(new Subtask(
                "SomeName",
                "SomeDescription",
                epicInMemory,
                LocalDateTime.now(),
                Duration.ofMinutes(1480)
        ));

        assertEquals(epicInMemory.getStatus(), TaskStatus.NEW);

        for (Subtask subtask : epicInMemory.getSubtaskArrayList()) {
            subtask.setStatus(TaskStatus.DONE);
        }

        assertEquals(epicInMemory.getStatus(), TaskStatus.DONE);

        for (Subtask subtask : epicInMemory.getSubtaskArrayList()) {
            subtask.setStatus(TaskStatus.NEW);
        }

        assertEquals(epicInMemory.getStatus(), TaskStatus.NEW);

        taskManager.updateSubtask(new Subtask(
                "Изменённое название",
                "Изменённое описание",
                3,
                epicInMemory,
                TaskStatus.IN_PROGRESS,
                LocalDateTime.now(),
                Duration.ofMinutes(60 * 48)
        ));

        assertEquals(epicInMemory.getStatus(), TaskStatus.IN_PROGRESS);

        taskManager.deleteSubtaskById(3);

        assertEquals(TaskStatus.NEW, epicInMemory.getStatus());

        for (int i = 1; i <= taskManager.getSubtasks().size(); i++) {
            taskManager.updateSubtask(new Subtask(
                    "Какое-то имя",
                    "Какое-то описание",
                    i,
                    epicInMemory,
                    TaskStatus.DONE,
                    LocalDateTime.now(),
                    Duration.ofMinutes(60 * 5)
            ));
        }

        assertEquals(TaskStatus.DONE, epicInMemory.getStatus());

    }

    private Task getTestTask() {
        return new Task("Разработка", "Разработать приложение \"Kanban-доска\"", TaskStatus.NEW);
    }

    private Subtask getTestSubtask(Epic epic) {
        return new Subtask("Написание кода", "Написать код", epic, LocalDateTime.now(), Duration.ofMinutes(180));
    }

    private Epic getTestEpic() {
        return new Epic("Тираж", "Тиражировать Kanban");
    }
}
