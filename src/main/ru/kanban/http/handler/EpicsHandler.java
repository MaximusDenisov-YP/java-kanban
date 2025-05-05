package ru.kanban.http.handler;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import ru.kanban.entity.Epic;
import ru.kanban.manager.TaskManager;
import ru.kanban.util.GsonUtil;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;

public class EpicsHandler extends BaseHttpHandler implements HttpHandler {
    private final TaskManager manager;
    private final Gson gson;

    public EpicsHandler(TaskManager manager) {
        this.manager = manager;
        this.gson = GsonUtil.getGson(manager);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            String path = exchange.getRequestURI().getPath();
            String method = exchange.getRequestMethod();
            String[] pathParts = path.split("/");

            if (pathParts.length == 3) {
                int id = Integer.parseInt(pathParts[2]);
                switch (method) {
                    case "GET" -> {
                        handleGetById(exchange, id);
                        return;
                    }
                    case "DELETE" -> {
                        handleDeleteById(exchange, id);
                        return;
                    }
                    default -> {
                        sendText(exchange, TEXT_405, 405);
                        return;
                    }
                }
            }

            if ("/epics".equals(path)) {
                switch (method) {
                    case "GET" -> handleGetAll(exchange);
                    case "POST" -> handlePost(exchange);
                    case "DELETE" -> handleDeleteAll(exchange);
                    default -> sendText(exchange, TEXT_405, 405);
                }
            } else {
                sendText(exchange, TEXT_404, 404);
            }
        } catch (NumberFormatException e) {
            sendText(exchange, TEXT_400, 400);
        } catch (Exception e) {
            sendText(exchange, String.format(TEXT_500, e.getMessage()), 500);
        }
    }

    private void handleGetAll(HttpExchange exchange) throws IOException {
        List<Epic> epics = manager.getEpics();
        sendText(exchange, gson.toJson(epics), 200);
    }

    private void handlePost(HttpExchange exchange) throws IOException {
        String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
        Epic epic = gson.fromJson(body, Epic.class);
        if (manager.getTaskById(epic.getId()).isPresent()) {
            manager.updateEpic(epic);
            sendText(exchange, "Task обновлён", 200);
        } else {
            manager.postEpic(epic);
            sendText(exchange, "Task добавлен", 201);
        }
    }

    private void handleDeleteAll(HttpExchange exchange) throws IOException {
        manager.deleteAllEpics();
        sendText(exchange, "Все epics удалены", 200);
    }

    private void handleGetById(HttpExchange exchange, int id) throws IOException {
        Optional<Epic> epic = manager.getEpicById(id);
        if (epic.isPresent()) {
            sendText(exchange, gson.toJson(epic.get()), 200);
        } else {
            sendNotFound(exchange, "Epic", id);
        }
    }

    private void handleDeleteById(HttpExchange exchange, int id) throws IOException {
        Epic removed = manager.deleteEpicById(id);
        if (removed != null) {
            sendText(exchange, "Epic удалён", 200);
        } else {
            sendNotFound(exchange, "Epic", id);
        }
    }
}