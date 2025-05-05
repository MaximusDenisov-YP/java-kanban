package ru.kanban.http.handler;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import ru.kanban.entity.Task;
import ru.kanban.manager.TaskManager;
import ru.kanban.util.GsonUtil;

import java.io.IOException;
import java.util.List;

public class HistoryHandler extends BaseHttpHandler implements HttpHandler {
    private final TaskManager manager;
    private final Gson gson;

    public HistoryHandler(TaskManager manager) {
        this.manager = manager;
        this.gson = GsonUtil.getGson(manager);
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
                sendText(exchange, "Метод не поддерживается. Используйте GET", 405);
            }
        } catch (Exception e) {
            e.printStackTrace();
            sendText(exchange, "{\"error\":\"" + e.getMessage() + "\"}", 500);
        }
    }
}