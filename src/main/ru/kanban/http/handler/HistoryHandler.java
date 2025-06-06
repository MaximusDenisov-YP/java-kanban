package ru.kanban.http.handler;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import ru.kanban.entity.Task;
import ru.kanban.manager.TaskManager;

import java.io.IOException;
import java.util.List;

public class HistoryHandler extends BaseHttpHandler {
    private final TaskManager manager;

    public HistoryHandler(TaskManager manager, Gson gson) {
        super(gson);
        this.manager = manager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String response;
        try {
            if ("GET".equalsIgnoreCase(exchange.getRequestMethod())) {
                List<Task> history = manager.getHistoryManager().getHistory();
                response = gson.toJson(history);
                sendText(exchange, response, 200);
            } else {
                sendMethodNotAllowed(exchange);
            }
        } catch (Exception e) {
            e.printStackTrace();
            sendText(exchange, "{\"error\":\"" + e.getMessage() + "\"}", 500);
        }
    }

    @Override
    protected String getPath() {
        return "history";
    }
}