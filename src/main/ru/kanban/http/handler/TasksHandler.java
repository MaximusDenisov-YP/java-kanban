package ru.kanban.http.handler;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import ru.kanban.entity.Task;
import ru.kanban.manager.TaskManager;
import ru.kanban.util.GsonUtil;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;

public class TasksHandler extends BaseHttpHandler implements HttpHandler {
    private final TaskManager manager;
    private final Gson gson;

    public TasksHandler(TaskManager manager) {
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

            if ("/tasks".equals(path)) {
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

    private void handlePost(HttpExchange exchange) throws IOException {
        String json = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
        Task task = gson.fromJson(json, Task.class);
        try {
            if (manager.getTaskById(task.getId()).isPresent()) {
                manager.updateTask(task);
                sendText(exchange, "Task обновлён", 200);
            } else {
                manager.postTask(task);
                sendText(exchange, "Task добавлен", 201);
            }
        } catch (RuntimeException e) {
            sendHasInteractions(exchange);
        }
    }

    private void handleGetAll(HttpExchange exchange) throws IOException {
        List<Task> tasks = manager.getTasks();
        sendText(exchange, gson.toJson(tasks), 200);
    }

    private void handleDeleteAll(HttpExchange exchange) throws IOException {
        manager.deleteAllTasks();
        sendText(exchange, "Все tasks удалены", 200);
    }

    private void handleGetById(HttpExchange exchange, int id) throws IOException {
        Optional<Task> task = manager.getTaskById(id);
        if (task.isPresent()) {
            sendText(exchange, gson.toJson(task.get()), 200);
        } else {
            sendNotFound(exchange, "Task", id);
        }
    }

    private void handleDeleteById(HttpExchange exchange, int id) throws IOException {
        Task removed = manager.deleteTaskById(id);
        if (removed != null) {
            sendText(exchange, "Task удален", 200);
        } else {
            sendNotFound(exchange, "Task", id);
        }
    }
}