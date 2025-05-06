package ru.kanban.http.handler;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import ru.kanban.entity.Subtask;
import ru.kanban.manager.TaskManager;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;

public class SubtasksHandler extends BaseHttpHandler {
    private final TaskManager manager;

    public SubtasksHandler(TaskManager manager, Gson gson) {
        super(gson);
        this.manager = manager;
    }

    @Override
    protected void handleGetAll(HttpExchange exchange) throws IOException {
        List<Subtask> subtasks = manager.getSubtasks();
        sendText(exchange, gson.toJson(subtasks), 200);
    }

    @Override
    protected void handlePost(HttpExchange exchange) throws IOException {
        InputStream input = exchange.getRequestBody();
        String json = new String(input.readAllBytes(), StandardCharsets.UTF_8);
        Subtask subtask = gson.fromJson(json, Subtask.class);
        try {
            if (manager.getSubtaskById(subtask.getId()).isPresent()) {
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

    @Override
    protected void handleDeleteAll(HttpExchange exchange) throws IOException {
        manager.deleteAllSubtasks();
        sendText(exchange, "Все subtasks удалены", 200);
    }

    @Override
    protected void handleGetById(HttpExchange exchange, int id) throws IOException {
        Optional<Subtask> subtask = manager.getSubtaskById(id);
        if (subtask.isPresent()) {
            sendText(exchange, gson.toJson(subtask.get()), 200);
        } else {
            sendNotFound(exchange, "Subtask", id);
        }
    }

    @Override
    protected void handleDeleteById(HttpExchange exchange, int id) throws IOException {
        Subtask removed = manager.deleteSubtaskById(id);
        if (removed != null) {
            sendText(exchange, "Subtask удалён", 200);
        } else {
            sendNotFound(exchange, "Subtask", id);
        }
    }

    @Override
    protected String getPath() {
        return "subtasks";
    }
}