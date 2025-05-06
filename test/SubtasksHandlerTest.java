import com.google.gson.Gson;
import org.junit.jupiter.api.*;
import ru.kanban.entity.Epic;
import ru.kanban.entity.Subtask;
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

class SubtasksHandlerTest {

    private static HttpTaskServer server;
    private static final TaskManager manager = Managers.getDefault();
    private static final Gson gson = GsonUtil.getGson(manager);
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
    void setUp() {
        manager.deleteAllSubtasks();
        manager.deleteAllEpics();
    }

    @Test
    void testCreateAndGetSubtask() throws IOException, InterruptedException {
        Epic epicToPut = new Epic("Epic", "Epic desc");
        manager.postEpic(epicToPut);
        Subtask subtask = new Subtask("Sub", "Sub desc", manager.getEpicById(0).get(), TaskStatus.NEW, LocalDateTime.now(), Duration.ofMinutes(15));

        System.out.println(subtask);
        String json = gson.toJson(subtask);
        System.out.println(json);
        HttpRequest postRequest = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/subtasks"))
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();
        HttpResponse<String> postResponse = client.send(postRequest, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, postResponse.statusCode());

        HttpRequest getRequest = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/subtasks"))
                .GET()
                .build();
        HttpResponse<String> getResponse = client.send(getRequest, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, getResponse.statusCode());

        List<Subtask> subtasks = gson.fromJson(getResponse.body(), JsonTypes.SUBTASK_LIST);
        assertEquals(1, subtasks.size());
        assertEquals("Sub", subtasks.get(0).getName());
    }

    @Test
    void testDeleteSubtaskById() throws IOException, InterruptedException {
        Epic epicToPost = new Epic("Epic", "Epic desc");
        manager.postEpic(epicToPost);
        manager.postSubtask(
                new Subtask("To Delete", "Desc", manager.getEpicById(0).get(), TaskStatus.NEW, LocalDateTime.now(), Duration.ofMinutes(10))
        );

        HttpRequest deleteRequest = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/subtasks/" + 1))
                .DELETE()
                .build();
        HttpResponse<String> deleteResponse = client.send(deleteRequest, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, deleteResponse.statusCode());
        assertTrue(manager.getSubtaskById(1).isEmpty());
    }
}