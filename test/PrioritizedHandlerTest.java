import com.google.gson.Gson;
import org.junit.jupiter.api.*;
import ru.kanban.entity.Task;
import ru.kanban.entity.TaskStatus;
import ru.kanban.http.HttpTaskServer;
import ru.kanban.manager.Managers;
import ru.kanban.manager.TaskManager;
import ru.kanban.util.GsonUtil;
import ru.kanban.util.JsonTypes;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PrioritizedHandlerTest {

    private static HttpTaskServer server;
    private static final TaskManager manager = Managers.getDefault();
    private static final Gson gson = GsonUtil.getGson(manager);
    private static final HttpClient client = HttpClient.newHttpClient();

    @BeforeAll
    static void startServer() throws IOException {
        server = new HttpTaskServer(manager);
        server.start();
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }

    @BeforeEach
    void setup() {
        manager.deleteAllTasks();
    }

    @Test
    void testGetPrioritizedTasks() throws IOException, InterruptedException {
        Task task1 = new Task("T1", "desc1", TaskStatus.NEW, LocalDateTime.now(), Duration.ofMinutes(30));
        Task task2 = new Task("T2", "desc2", TaskStatus.NEW, LocalDateTime.now().plusMinutes(60), Duration.ofMinutes(20));
        manager.postTask(task1);
        manager.postTask(task2);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/prioritized"))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());

        List<Task> result = gson.fromJson(response.body(), JsonTypes.TASK_LIST);
        assertEquals(2, result.size());
        assertEquals("T1", result.get(0).getName());
        assertEquals("T2", result.get(1).getName());
    }
}