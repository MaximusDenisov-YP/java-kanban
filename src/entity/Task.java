package entity;

import java.util.Objects;

public class Task {
    private String name;
    private String description;
    private final int id;
    private TaskStatus status;

    public Task(String name, String description, int id, TaskStatus taskStatus) {
        this.name = name;
        this.description = description;
        this.id = id;
        this.status = taskStatus;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getId() {
        return id;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public void setStatus(TaskStatus taskStatus) {
        this.status = taskStatus;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || this.getClass() != obj.getClass()) return false;
        Task someTask = (Task) obj;
        return this.id == someTask.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, description, id, status);
    }

    @Override
    public String toString() {
        String sampleBody = "\nID = %d\nНаименование задачи: %s\nОписание задачи: %s\nТип задачи: Task";
        return String.format(sampleBody, id, name, description);
    }

}
