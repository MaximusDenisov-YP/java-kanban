package ru.kanban.entity;

import java.time.Duration;
import java.time.LocalDateTime;

public class Subtask extends Task {
    private final Epic epic;

    public Subtask(
            String name,
            String description,
            Epic epic,
            LocalDateTime startTime,
            Duration duration
    ) {
        super(name, description, TaskStatus.NEW, startTime, duration);
        this.epic = epic;
    }

    public Subtask(
            String name,
            String description,
            int id,
            Epic epic,
            TaskStatus taskStatus,
            LocalDateTime startTime,
            Duration duration
    ) {
        super(name, description, id, taskStatus, startTime, duration);
        this.epic = epic;
    }

    public Subtask(Subtask subtask) {
        super(
                subtask.getName(),
                subtask.getDescription(),
                subtask.getId(),
                subtask.getStatus(),
                subtask.getStartTime(),
                subtask.getDuration()
        );
        this.epic = subtask.getEpic();
    }

    public Epic getEpic() {
        return epic;
    }

    @Override
    public void setStatus(TaskStatus taskStatus) {
        super.setStatus(taskStatus);
        epic.checkEpicStatus();
    }

    @Override
    public String toString() {
        return "SubTask" + super.toString()
                .substring(4);
    }

}
