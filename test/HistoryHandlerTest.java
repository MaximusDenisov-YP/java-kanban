import org.junit.jupiter.api.*;
import ru.kanban.entity.Epic;
import ru.kanban.entity.Subtask;
import ru.kanban.entity.Task;
import ru.kanban.entity.TaskStatus;
import ru.kanban.http.HttpTaskServer;
import ru.kanban.manager.Managers;
import ru.kanban.manager.TaskManager;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class HistoryHandlerTest {

    private static HttpTaskServer server;
    private static final TaskManager manager = Managers.getDefault();
    private static final HttpClient client = HttpClient.newHttpClient();

    @BeforeAll
    static void beforeAll() throws IOException {
        server = new HttpTaskServer(manager);
        server.start();
    }

    @AfterAll
    static void afterAll() {
        server.stop();
    }

    @BeforeEach
    void clearHistory() {
        manager.deleteAllTasks();
        manager.deleteAllEpics();
        manager.deleteAllSubtasks();
    }

    @Test
    void testHistoryAfterViewingTasks() throws IOException, InterruptedException {
        Task task = new Task("Task1", "Desc1", TaskStatus.NEW, LocalDateTime.now(), Duration.ofMinutes(10));
        manager.postTask(task);

        Epic epic = new Epic("Epic1", "EpicDesc");
        manager.postEpic(epic);

        Subtask subtask = new Subtask("Subtask1", "SubDesc", manager.getEpicById(1).get(), TaskStatus.DONE, LocalDateTime.now().plusDays(1), Duration.ofMinutes(15));
        manager.postSubtask(subtask);

        manager.getTaskById(task.getId());
        manager.getEpicById(epic.getId());
        manager.getSubtaskById(2);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/history"))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());

        String responseBody = response.body();
        System.out.println(responseBody);
        assertTrue(responseBody.contains("Task1"));
        assertTrue(responseBody.contains("Epic1"));
        assertTrue(responseBody.contains("Subtask1"));
    }

    @Test
    void testEmptyHistoryReturnsEmptyList() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/history"))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        assertEquals("[]", response.body());
    }
}
