import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.kanban.entity.Epic;
import ru.kanban.entity.Subtask;
import ru.kanban.entity.Task;
import ru.kanban.entity.TaskStatus;
import ru.kanban.manager.FileBackedTaskManager;

import java.io.*;
import java.nio.file.Files;
import java.time.Duration;
import java.time.LocalDateTime;

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
    @DisplayName("Проверка корректной записи задач в пустой автосейв")
    void addCorrectWriteTaskToTaskMemoryManager() {
        taskManager = FileBackedTaskManager.loadFromFile(autosave);
        taskManager.postTask(
                new Task(
                        "Разработка",
                        "Разработать приложение \"Kanban-доска\"",
                        TaskStatus.NEW,
                        LocalDateTime.parse("2025-04-24T21:05:51.055692"),
                        Duration.ofMinutes(180))
        );
        assertEquals(
                "0,TASK,Разработка,NEW,Разработать приложение \"Kanban-доска\",2025-04-24T21:05:51.055692,PT3H,\n",
                readStringFromFile(taskManager.getAutosave())
        );
        taskManager.postEpic(new Epic("Новый эпик", "Его описание"));
        assertEquals(
                """
                        0,TASK,Разработка,NEW,Разработать приложение "Kanban-доска",2025-04-24T21:05:51.055692,PT3H,
                        1,EPIC,Новый эпик,NEW,Его описание,null,null,
                        """,
                readStringFromFile(taskManager.getAutosave())
        );
        taskManager.postSubtask(
                new Subtask("Новый сабтаск",
                        "Описание сабтаска",
                        taskManager.getEpicById(1),
                        LocalDateTime.parse("2025-04-24T21:05:51.055692"),
                        Duration.ofMinutes(60)
                )
        );
        assertEquals(
                """
                        0,TASK,Разработка,NEW,Разработать приложение "Kanban-доска",2025-04-24T21:05:51.055692,PT3H,
                        1,EPIC,Новый эпик,NEW,Его описание,2025-04-24T21:05:51.055692,PT1H,
                        2,SUBTASK,Новый сабтаск,NEW,Описание сабтаска,2025-04-24T21:05:51.055692,PT1H,1
                        """,
                readStringFromFile(taskManager.getAutosave())
        );

        System.out.println(readStringFromFile(autosave));
    }

    @Test
    @DisplayName("Проверка корректной обработки/чтения наполненного автосейва")
    void addCorrectTaskMemoryToTaskManager() {
        String strForAutosave =
                """
                        1,TASK,Task1,NEW,Description task1,2025-04-25T20:01:49.500465,PT1H
                        2,EPIC,Epic2,DONE,Description epic2,2025-04-26T20:01:49.500465,PT2H
                        3,SUBTASK,Sub Task2,DONE,Description sub task3,2025-04-27T20:01:49.500465,PT1H,2
                        
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
                        1,TASK,Task1,NEW,Description task1,2025-04-25T20:01:49.500465,PT1H
                        2,EPIC,Epic2,DONE,Description epic2,2025-04-25T20:01:49.500465,PT1H
                        3,SUBTASK,Sub Task2,DONE,Description sub task3,2025-04-25T20:01:49.500465,PT1H,2
                        
                        """;

        File filledFile = writeStringToFile(strForAutosave, autosave);
        taskManager = FileBackedTaskManager.loadFromFile(filledFile);
        taskManager.deleteTaskById(1);
        assertEquals(
                readStringFromFile(taskManager.getAutosave()),
                """
                        2,EPIC,Epic2,DONE,Description epic2,2025-04-25T20:01:49.500465,PT1H,
                        3,SUBTASK,Sub Task2,DONE,Description sub task3,2025-04-25T20:01:49.500465,PT1H,2
                        """
        );
        taskManager.deleteSubtaskById(3);
        assertEquals(
                readStringFromFile(taskManager.getAutosave()),
                "2,EPIC,Epic2,NEW,Description epic2,null,null,\n"
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
                        1,TASK,Task1,NEW,Description task1,2025-04-25T20:01:49.500465,PT1H
                        2,EPIC,Epic2,DONE,Description epic2,2025-04-25T20:01:49.500465,PT1H
                        3,SUBTASK,Sub Task2,DONE,Description sub task3,2025-04-25T20:01:49.500465,PT1H,2
                        
                        """;

        taskManager = FileBackedTaskManager.loadFromFile(writeStringToFile(strForAutosave, autosave));
        taskManager.postTask(getTestTask());
        assertEquals(
                readStringFromFile(taskManager.getAutosave()),
                """
                        0,TASK,Разработка,NEW,Разработать приложение "Kanban-доска",2025-04-24T20:48:18.371191,PT1H,
                        1,TASK,Task1,NEW,Description task1,2025-04-25T20:01:49.500465,PT1H,
                        2,EPIC,Epic2,DONE,Description epic2,2025-04-25T20:01:49.500465,PT1H,
                        3,SUBTASK,Sub Task2,DONE,Description sub task3,2025-04-25T20:01:49.500465,PT1H,2
                        """
        );
        taskManager.deleteAllTasks();
        assertEquals(
                readStringFromFile(taskManager.getAutosave()),
                """
                        2,EPIC,Epic2,DONE,Description epic2,2025-04-25T20:01:49.500465,PT1H,
                        3,SUBTASK,Sub Task2,DONE,Description sub task3,2025-04-25T20:01:49.500465,PT1H,2
                        """
        );
    }

    @Test
    @DisplayName("Проверка удаления привязанной Subtask и Epic при его удалении из автосейва")
    void deleteCorrectSubtasksWithEpicFromTaskManagerWithMemory() {
        String strForAutosave =
                """
                        1,TASK,Task1,NEW,Description task1,2025-04-25T20:01:49.500465,PT1H
                        2,EPIC,Epic2,DONE,Description epic2,2025-04-25T20:01:49.500465,PT1H
                        3,SUBTASK,Sub Task2,DONE,Description sub task3,2025-04-25T20:01:49.500465,PT1H,2
                        
                        """;

        taskManager = FileBackedTaskManager.loadFromFile(writeStringToFile(strForAutosave, autosave));
        taskManager.deleteEpicById(2);
        assertEquals(
                "1,TASK,Task1,NEW,Description task1,2025-04-25T20:01:49.500465,PT1H,\n",
                readStringFromFile(taskManager.getAutosave())
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
        return new Task("Разработка", "Разработать приложение \"Kanban-доска\"", TaskStatus.NEW, LocalDateTime.parse("2025-04-24T20:48:18.371191"), Duration.ofMinutes(60));
    }
}