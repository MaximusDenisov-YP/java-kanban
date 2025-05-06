package ru.kanban.http.handler;

import com.sun.net.httpserver.HttpExchange;
import ru.kanban.entity.Epic;
import ru.kanban.manager.TaskManager;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;

public class EpicsHandler extends BaseHttpHandler {
    private final TaskManager manager;

    public EpicsHandler(TaskManager manager) {
        this.manager = manager;
    }

    @Override
    protected void handleGetAll(HttpExchange exchange) throws IOException {
        List<Epic> epics = manager.getEpics();
        sendText(exchange, gson.toJson(epics), 200);
    }

    @Override
    protected void handlePost(HttpExchange exchange) throws IOException {
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

    @Override
    protected void handleDeleteAll(HttpExchange exchange) throws IOException {
        manager.deleteAllEpics();
        sendText(exchange, "Все epics удалены", 200);
    }

    @Override
    protected void handleGetById(HttpExchange exchange, int id) throws IOException {
        Optional<Epic> epic = manager.getEpicById(id);
        if (epic.isPresent()) {
            sendText(exchange, gson.toJson(epic.get()), 200);
        } else {
            sendNotFound(exchange, "Epic", id);
        }
    }

    @Override
    protected void handleDeleteById(HttpExchange exchange, int id) throws IOException {
        Epic removed = manager.deleteEpicById(id);
        if (removed != null) {
            sendText(exchange, "Epic удалён", 200);
        } else {
            sendNotFound(exchange, "Epic", id);
        }
    }

    @Override
    protected String getPath() {
        return "epics";
    }
}