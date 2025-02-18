package ru.kanban.manager;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.*;
import ru.kanban.entity.Epic;
import ru.kanban.entity.Subtask;
import ru.kanban.entity.Task;
import ru.kanban.entity.TaskStatus;

public class TaskManagerTest {

    private TaskManager taskManager;
    private HistoryManager historyManager;

    @BeforeEach
    void beforeEach() {
        taskManager = Managers.getDefault();
        historyManager = Managers.getDefaultHistory();
    }

    @Test
    @DisplayName("Проверка корректного создания Task")
    void addCorrectTaskToTaskList() {
        Task task = getTestTask();
        taskManager.postTask(task);
        assertFalse(taskManager.getTasks().isEmpty());
        assertEquals(task, taskManager.getTaskById(0));
    }

    @Test
    @DisplayName("Проверка корректного создания Epic")
    void addCorrectEpicToEpicList() {
        Epic epic = getTestEpic();
        taskManager.postEpic(epic);
        assertFalse(taskManager.getEpics().isEmpty());
        assertEquals(epic, taskManager.getEpicById(0));
    }

    @Test
    @DisplayName("Проверка корректного создания Subtask")
    void addCorrectSubtaskToEpicList() {
        Epic epic = getTestEpic();
        Subtask subtask = getTestSubtask(epic);
        taskManager.postEpic(epic);
        taskManager.postSubtask(subtask);
        assertFalse(taskManager.getSubtasks().isEmpty());
        assertEquals(subtask, taskManager.getSubtaskById(1));
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
        assertEquals(subtask, taskManager.getSubtaskById(1));
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
