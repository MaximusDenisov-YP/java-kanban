package ru.kanban.http.handler;

import com.sun.net.httpserver.HttpExchange;
import ru.kanban.entity.Task;
import ru.kanban.manager.TaskManager;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;

public class TasksHandler extends BaseHttpHandler {
    private final TaskManager manager;

    public TasksHandler(TaskManager manager) {
        this.manager = manager;
    }

    @Override
    protected void handlePost(HttpExchange exchange) throws IOException {
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

    @Override
    protected void handleGetAll(HttpExchange exchange) throws IOException {
        List<Task> tasks = manager.getTasks();
        sendText(exchange, gson.toJson(tasks), 200);
    }

    @Override
    protected void handleDeleteAll(HttpExchange exchange) throws IOException {
        manager.deleteAllTasks();
        sendText(exchange, "Все tasks удалены", 200);
    }

    @Override
    protected void handleGetById(HttpExchange exchange, int id) throws IOException {
        Optional<Task> task = manager.getTaskById(id);
        if (task.isPresent()) {
            sendText(exchange, gson.toJson(task.get()), 200);
        } else {
            sendNotFound(exchange, "Task", id);
        }
    }

    @Override
    protected void handleDeleteById(HttpExchange exchange, int id) throws IOException {
        Task removed = manager.deleteTaskById(id);
        if (removed != null) {
            sendText(exchange, "Task удален", 200);
        } else {
            sendNotFound(exchange, "Task", id);
        }
    }

    @Override
    protected String getPath() {
        return "tasks";
    }
}