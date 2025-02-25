package ru.kanban.manager;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.*;
import ru.kanban.entity.Epic;
import ru.kanban.entity.Subtask;
import ru.kanban.entity.Task;
import ru.kanban.entity.TaskStatus;

import java.util.List;

public class TaskManagerTest {

    private TaskManager taskManager;
    private HistoryManager historyManager;

    @BeforeEach
    void beforeEach() {
        taskManager = new InMemoryTaskManager();
        historyManager = new InMemoryHistoryManager();
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
    @DisplayName("Проверка корректного удаления Epic по ID")
    void deleteCorrectEpicByIdInEpicList() {
        Epic epic = getTestEpic();
        taskManager.postEpic(epic);
        assertFalse(taskManager.getEpics().isEmpty());
        taskManager.deleteEpicById(0);
        assertTrue(taskManager.getEpics().isEmpty());
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
        Subtask subtaskToChange = new Subtask("a", "b", epic);
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
    @DisplayName("Утилитарный класс возвращает корректно инициализированные менеджеры")
    void utilityClassReturnsInitializedManagers() {
        TaskManager manager1 = Managers.getDefault();
        TaskManager manager2 = Managers.getDefault();
        HistoryManager history1 = Managers.getDefaultHistory();
        HistoryManager history2 = Managers.getDefaultHistory();

        assertEquals(manager1, manager2, "Ожидается один и тот же экземпляр TaskManager");
        assertEquals(history1, history2, "Ожидается один и тот же экземпляр HistoryManager");
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
        Managers.getDefaultHistory().clearHistory();

        Task task = getTestTask();
        taskManager.postTask(task);
        taskManager.getTaskById(task.getId());

        task.setName("Updated Name");
        task.setDescription("Updated Description");
        task.setStatus(TaskStatus.IN_PROGRESS);

        List<Task> history = Managers.getDefaultHistory().getHistory();
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
    @DisplayName("История может хранить только до 10 задач включительно")
    void historyManagerMakeSavesOnlyTenTasks() {
        taskManager.postTask(getTestTask());
        Epic epic = getTestEpic();
        taskManager.postEpic(epic);
        taskManager.postSubtask(getTestSubtask(epic));

        for (int i = 0; i < 3; i++) {
            taskManager.getTaskById(0);
            taskManager.getEpicById(1);
            taskManager.getSubtaskById(2);
        }

        taskManager.getTaskById(0);
        taskManager.getTaskById(0);

        assertEquals(
                Managers.getDefaultHistory().getHistory().size(),
                10,
                "В истории операции должно быть максимум 10 значений"
        );
        assertEquals(
                1,
                Managers.getDefaultHistory().getHistory().get(0).getId(),
                "ID значения должен быть 1, так как задача с 0 ID, после добавления 11-го элемента, должна была удалиться");

    }

    private Task getTestTask() {
        return new Task("Разработка", "Разработать приложение \"Kanban-доска\"", TaskStatus.NEW);
    }

    private Subtask getTestSubtask(Epic epic) {
        return new Subtask("Написание кода", "Написать код", epic);
    }

    private Epic getTestEpic() {
        return new Epic("Тираж", "Тиражировать Kanban");
    }
}
