package ru.kanban.http.handler;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import ru.kanban.entity.Task;
import ru.kanban.manager.TaskManager;

import java.io.IOException;
import java.util.List;

public class PrioritizedHandler extends BaseHttpHandler {
    private final TaskManager manager;

    public PrioritizedHandler(TaskManager manager, Gson gson) {
        super(gson);
        this.manager = manager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if ("GET".equalsIgnoreCase(exchange.getRequestMethod())) {
            List<Task> prioritizedTasks = manager.getPrioritizedTasks();
            sendText(exchange, gson.toJson(prioritizedTasks), 200);
        } else {
            sendMethodNotAllowed(exchange);
        }
    }

    @Override
    protected String getPath() {
        return "prioritized";
    }
}
