package ru.kanban.http.handler;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import ru.kanban.entity.Subtask;
import ru.kanban.manager.TaskManager;
import ru.kanban.util.GsonUtil;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;

public class SubtasksHandler extends BaseHttpHandler implements HttpHandler {
    private final TaskManager manager;
    private final Gson gson;

    public SubtasksHandler(TaskManager manager) {
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

            if ("/subtasks".equals(path)) {
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
        List<Subtask> subtasks = manager.getSubtasks();
        sendText(exchange, gson.toJson(subtasks), 200);
    }

    private void handlePost(HttpExchange exchange) throws IOException {
        InputStream input = exchange.getRequestBody();
        String json = new String(input.readAllBytes(), StandardCharsets.UTF_8);
        Subtask subtask = gson.fromJson(json, Subtask.class);
        try {
            if (manager.getTaskById(subtask.getId()).isPresent()) {
                manager.updateSubtask(subtask);
                sendText(exchange, "Subtask обновлён", 200);
            } else {
                manager.postSubtask(subtask);
                sendText(exchange, "Subtask добавлен", 201);
            }
        } catch (RuntimeException e) {
            sendHasInteractions(exchange);
        }
    }

    private void handleDeleteAll(HttpExchange exchange) throws IOException {
        manager.deleteAllSubtasks();
        sendText(exchange, "Все subtasks удалены", 200);
    }

    private void handleGetById(HttpExchange exchange, int id) throws IOException {
        Optional<Subtask> subtask = manager.getSubtaskById(id);
        if (subtask.isPresent()) {
            sendText(exchange, gson.toJson(subtask.get()), 200);
        } else {
            sendNotFound(exchange, "Subtask", id);
        }
    }

    private void handleDeleteById(HttpExchange exchange, int id) throws IOException {
        Subtask removed = manager.deleteSubtaskById(id);
        if (removed != null) {
            sendText(exchange, "Subtask удалён", 200);
        } else {
            sendNotFound(exchange, "Subtask", id);
        }
    }
}