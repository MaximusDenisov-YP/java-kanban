package ru.kanban.http.handler;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import ru.kanban.manager.Managers;
import ru.kanban.util.GsonUtil;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class BaseHttpHandler implements HttpHandler {

    protected static final String TEXT_405 = "{\"error\":\"Метод не поддерживается\"}";
    protected static final String TEXT_400 = "{\"error\":\"Некорректный формат ID в пути\"}";
    protected static final String TEXT_404 = "{\"error\":\"Некорректный путь запроса\"}";
    protected static final String TEXT_500 = "{\"error\":\"Ошибка сервера: %s\"}";

    protected Gson gson = GsonUtil.getGson(Managers.getDefault());

    protected void sendText(HttpExchange exchange, String text, int statusCode) throws IOException {
        byte[] response = text.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().add("Content-Type", "application/json; charset=UTF-8");
        exchange.sendResponseHeaders(statusCode, response.length);
        exchange.getResponseBody().write(response);
        exchange.close();
    }

    protected void sendNotFound(HttpExchange exchange, String entityName, int id) throws IOException {
        JsonObject jsonBlank = new JsonObject();
        jsonBlank.addProperty("error", "%s с ID %d не найден.");
        sendText(exchange, String.format(jsonBlank.toString(), entityName, id), 404);
    }

    protected void sendHasInteractions(HttpExchange exchange) throws IOException {
        JsonObject json = new JsonObject();
        json.addProperty("error", "Новая задача пересекается по времени с уже существующей.");
        sendText(exchange, json.toString(), 406);
    }

    protected void sendMethodNotAllowed(HttpExchange exchange) throws IOException {
        JsonObject json = new JsonObject();
        json.addProperty("error", TEXT_405);
        sendText(exchange, json.toString(), 405);
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
                    case "GET" -> handleGetById(exchange, id);
                    case "DELETE" -> handleDeleteById(exchange, id);
                    default -> sendText(exchange, TEXT_405, 405);
                }
                return;
            }

            if (("/" + getPath()).equals(path)) {
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

    protected void handleGetAll(HttpExchange exchange) throws IOException {
        sendMethodNotAllowed(exchange);
    }

    protected void handlePost(HttpExchange exchange) throws IOException {
        sendMethodNotAllowed(exchange);
    }

    protected void handleDeleteAll(HttpExchange exchange) throws IOException {
        sendMethodNotAllowed(exchange);
    }

    protected void handleGetById(HttpExchange exchange, int id) throws IOException {
        sendMethodNotAllowed(exchange);
    }

    protected void handleDeleteById(HttpExchange exchange, int id) throws IOException {
        sendMethodNotAllowed(exchange);
    }

    protected String getPath() {
        return null;
    }
}