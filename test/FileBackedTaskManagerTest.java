import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.kanban.entity.Task;
import ru.kanban.entity.TaskStatus;
import ru.kanban.manager.FileBackedTaskManager;

import java.io.*;
import java.nio.file.Files;

import static org.junit.jupiter.api.Assertions.*;

public class FileBackedTaskManagerTest {

    private FileBackedTaskManager taskManager;

    private File autosave;

    @BeforeEach
    void beforeEach() {
        taskManager = new FileBackedTaskManager();
        try {
            autosave = Files.createTempFile(null, null).toFile();
        } catch (IOException exc) {
            fail("Не получилось записать пустой файл автосейва");
        }
    }

    @Test
    @DisplayName("Проверка корректной обработки/чтения наполненного автосейва")
    void addCorrectTaskMemoryToTaskManager() {
        String strForAutosave =
                """
                        1,TASK,Task1,NEW,Description task1,
                        2,EPIC,Epic2,DONE,Description epic2,
                        3,SUBTASK,Sub Task2,DONE,Description sub task3,2
                        """;
        taskManager = FileBackedTaskManager.loadFromFile(writeStringToFile(strForAutosave, autosave));
        assertFalse(taskManager.getTasks().isEmpty());
        assertFalse(taskManager.getEpics().isEmpty());
        assertFalse(taskManager.getSubtasks().isEmpty());
    }

    @Test
    @DisplayName("Проверка корректной обработки/чтения пустого автосейва")
    void addCorrectEmptyTaskMemoryToTaskManager() {
        taskManager = FileBackedTaskManager.loadFromFile(autosave);
        assertTrue(taskManager.getTasks().isEmpty());
        assertTrue(taskManager.getEpics().isEmpty());
        assertTrue(taskManager.getSubtasks().isEmpty());
    }

    @Test
    @DisplayName("Проверка удаления задач поочерёдно из автосейва")
    void deleteCorrectAnyTaskFromTaskManagerWithMemory() {
        String strForAutosave =
                """
                        1,TASK,Task1,NEW,Description task1,
                        2,EPIC,Epic2,DONE,Description epic2,
                        3,SUBTASK,Sub Task2,DONE,Description sub task3,2
                        """;

        File filledFile = writeStringToFile(strForAutosave, autosave);
        taskManager = FileBackedTaskManager.loadFromFile(filledFile);
        taskManager.deleteTaskById(1);
        assertEquals(
                readStringFromFile(taskManager.getAutosave()),
                """
                        2,EPIC,Epic2,DONE,Description epic2,
                        3,SUBTASK,Sub Task2,DONE,Description sub task3,2
                        """
        );
        taskManager.deleteSubtaskById(3);
        assertEquals(
                readStringFromFile(taskManager.getAutosave()),
                "2,EPIC,Epic2,NEW,Description epic2,\n"
        );
        taskManager.deleteEpicById(2);
        assertEquals(
                readStringFromFile(taskManager.getAutosave()),
                ""
        );
    }

    @Test
    @DisplayName("Проверка удаления всех Task из автосейва")
    void deleteCorrectAllTasksFromTaskManagerWithMemory() {
        String strForAutosave =
                """
                        1,TASK,Task1,NEW,Description task1,
                        2,EPIC,Epic2,DONE,Description epic2,
                        3,SUBTASK,Sub Task2,DONE,Description sub task3,2
                        """;

        taskManager = FileBackedTaskManager.loadFromFile(writeStringToFile(strForAutosave, autosave));
        taskManager.postTask(getTestTask());
        assertEquals(
                readStringFromFile(taskManager.getAutosave()),
                """
                        0,TASK,Разработка,NEW,Разработать приложение "Kanban-доска",
                        1,TASK,Task1,NEW,Description task1,
                        2,EPIC,Epic2,DONE,Description epic2,
                        3,SUBTASK,Sub Task2,DONE,Description sub task3,2
                        """
        );
        taskManager.deleteAllTasks();
        assertEquals(
                readStringFromFile(taskManager.getAutosave()),
                """
                        2,EPIC,Epic2,DONE,Description epic2,
                        3,SUBTASK,Sub Task2,DONE,Description sub task3,2
                        """
        );
    }

    @Test
    @DisplayName("Проверка удаления привязанной Subtask и Epic при его удалении из автосейва")
    void deleteCorrectSubtasksWithEpicFromTaskManagerWithMemory() {
        String strForAutosave =
                """
                        1,TASK,Task1,NEW,Description task1,
                        2,EPIC,Epic2,DONE,Description epic2,
                        3,SUBTASK,Sub Task2,DONE,Description sub task3,2
                        """;

        taskManager = FileBackedTaskManager.loadFromFile(writeStringToFile(strForAutosave, autosave));
        taskManager.deleteEpicById(2);
        assertEquals(
                readStringFromFile(taskManager.getAutosave()),
                "1,TASK,Task1,NEW,Description task1,\n"
        );
    }


    private String readStringFromFile(File file) {
        StringBuilder sb = new StringBuilder();
        try (BufferedReader bf = new BufferedReader(new FileReader(file))) {
            while (bf.ready()) {
                sb.append(bf.readLine()).append("\n");
            }
        } catch (IOException exc) {
            fail("Ошибка записи в файл автосейва");
        }
        return sb.toString();
    }

    private File writeStringToFile(String str, File file) {
        try (BufferedWriter bf = new BufferedWriter(new FileWriter(file))) {
            bf.write(str);
        } catch (IOException exc) {
            fail("Ошибка записи в файл автосейва");
        }
        return file;
    }

    private Task getTestTask() {
        return new Task("Разработка", "Разработать приложение \"Kanban-доска\"", TaskStatus.NEW);
    }
}