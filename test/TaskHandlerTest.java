import com.google.gson.Gson;
import org.junit.jupiter.api.*;
import ru.kanban.entity.Task;
import ru.kanban.entity.TaskStatus;
import ru.kanban.http.HttpTaskServer;
import ru.kanban.manager.InMemoryTaskManager;
import ru.kanban.manager.TaskManager;
import ru.kanban.util.GsonUtil;

import java.io.IOException;
import java.net.URI;
import java.net.http.*;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class TaskHandlerTest {
    private TaskManager manager;
    private HttpTaskServer server;
    private Gson gson;

    @BeforeEach
    public void setUp() throws IOException {
        manager = new InMemoryTaskManager();
        server = new HttpTaskServer(manager);
        this.gson = GsonUtil.getGson(manager);
        server.start();
    }

    @AfterEach
    public void tearDown() {
        server.stop();
    }

    @Test
    public void testPostAndGetTask() throws IOException, InterruptedException {
        Task task = new Task("Test", "Test Desc", TaskStatus.NEW,
                LocalDateTime.now(), Duration.ofMinutes(10));
        String json = gson.toJson(task);
        HttpClient client = HttpClient.newHttpClient();

        HttpRequest postRequest = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks"))
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        HttpResponse<String> postResponse = client.send(postRequest, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, postResponse.statusCode());

        HttpRequest getRequest = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks"))
                .GET()
                .build();

        HttpResponse<String> getResponse = client.send(getRequest, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, getResponse.statusCode());

        Task[] tasks = gson.fromJson(getResponse.body(), Task[].class);
        assertEquals(1, tasks.length);
        assertEquals("Test", tasks[0].getName());
    }

    @Test
    public void testDeleteTasks() throws IOException, InterruptedException {
        Task task = new Task("ToDelete", "desc", TaskStatus.NEW,
                LocalDateTime.now(), Duration.ofMinutes(5));
        manager.postTask(task);
        HttpClient client = HttpClient.newHttpClient();

        HttpRequest deleteRequest = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks"))
                .DELETE()
                .build();

        HttpResponse<String> deleteResponse = client.send(deleteRequest, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, deleteResponse.statusCode());

        List<Task> tasks = manager.getTasks();
        assertTrue(tasks.isEmpty());
    }
}