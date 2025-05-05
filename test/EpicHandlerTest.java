import com.google.gson.Gson;
import org.junit.jupiter.api.*;
import ru.kanban.entity.Epic;
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
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class EpicHandlerTest {

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
        manager.deleteAllEpics();
    }

    @Test
    void testCreateAndGetEpic() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic name", "Epic description");
        String json = gson.toJson(epic);
        HttpRequest postRequest = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/epics"))
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();
        HttpResponse<String> postResponse = client.send(postRequest, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, postResponse.statusCode());

        HttpRequest getRequest = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/epics"))
                .GET()
                .build();
        HttpResponse<String> getResponse = client.send(getRequest, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, getResponse.statusCode());

        List<Epic> epics = gson.fromJson(getResponse.body(), JsonTypes.EPIC_LIST);
        assertEquals(1, epics.size());
        assertEquals("Epic name", epics.get(0).getName());
    }

    @Test
    void testGetEpicByIdAndDelete() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic to delete", "Desc");
        manager.postEpic(epic);

        HttpRequest getByIdRequest = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/epics/0"))
                .GET()
                .build();
        HttpResponse<String> getByIdResponse = client.send(getByIdRequest, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, getByIdResponse.statusCode());

        HttpRequest deleteRequest = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/epics/0"))
                .DELETE()
                .build();
        HttpResponse<String> deleteResponse = client.send(deleteRequest, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, deleteResponse.statusCode());

        HttpRequest getAfterDelete = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/epics/0"))
                .GET()
                .build();
        HttpResponse<String> getDeletedResponse = client.send(getAfterDelete, HttpResponse.BodyHandlers.ofString());
        assertEquals(404, getDeletedResponse.statusCode());
    }

    @Test
    void testDeleteAllEpics() throws IOException, InterruptedException {
        manager.postEpic(new Epic("Epic1", "Desc1"));
        manager.postEpic(new Epic("Epic2", "Desc2"));

        HttpRequest deleteRequest = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/epics"))
                .DELETE()
                .build();
        HttpResponse<String> deleteResponse = client.send(deleteRequest, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, deleteResponse.statusCode());

        HttpRequest getRequest = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/epics"))
                .GET()
                .build();
        HttpResponse<String> getResponse = client.send(getRequest, HttpResponse.BodyHandlers.ofString());

        List<Epic> epics = gson.fromJson(getResponse.body(), JsonTypes.EPIC_LIST);
        assertTrue(epics.isEmpty());
    }
}
