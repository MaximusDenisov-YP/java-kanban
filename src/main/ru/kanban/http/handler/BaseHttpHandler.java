package ru.kanban.http.handler;

import com.google.gson.JsonObject;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class BaseHttpHandler {

    protected final static String TEXT_405 = "{\"error\":\"Метод не поддерживается\"}";
    protected final static String TEXT_400 = "{\"error\":\"Некорректный формат ID в пути\"}";
    protected final static String TEXT_404 = "{\"error\":\"Некорректный путь запроса\"}";
    protected final static String TEXT_500 = "{\"error\":\"Ошибка сервера: %s\"}";

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

}