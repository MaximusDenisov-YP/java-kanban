package ru.kanban.exception;

public class ManagerSaveException extends RuntimeException {
    public ManagerSaveException(String str) {
        super(str);
    }
}
