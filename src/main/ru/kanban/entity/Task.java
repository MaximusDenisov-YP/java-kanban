package ru.kanban.entity;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;

public class Task implements Comparable<Task> {
    private String name;
    private String description;
    private int id;
    private TaskStatus status;
    private LocalDateTime startTime;
    private Duration duration;

    public Task(String name, String description, TaskStatus taskStatus, LocalDateTime startTime, Duration duration) {
        this.name = name;
        this.description = description;
        this.status = taskStatus;
        this.startTime = startTime;
        this.duration = duration;
    }

    public Task(String name, String description, int id, TaskStatus taskStatus, LocalDateTime startTime, Duration duration) {
        this.name = name;
        this.description = description;
        this.status = taskStatus;
        this.id = id;
        this.startTime = startTime;
        this.duration = duration;
    }

    public Task(Task task) {
        this.name = task.name;
        this.description = task.description;
        this.status = task.status;
        this.id = task.id;
        this.startTime = task.startTime;
        this.duration = task.duration;
    }

    public Task(String name, String description, int id, TaskStatus taskStatus) {
        this.name = name;
        this.description = description;
        this.status = taskStatus;
        this.id = id;
    }

    public Task(String name, String description, TaskStatus taskStatus) {
        this.name = name;
        this.description = description;
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

    public void setId(int id) {
        this.id = id;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public void setStatus(TaskStatus taskStatus) {
        this.status = taskStatus;
    }

    public Duration getDuration() {
        return duration;
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return startTime.plus(duration);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, description, id, status);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        Task someTask = (Task) obj;
        return this.id == someTask.id;
    }

    @Override
    public String toString() {
        Optional<LocalDateTime> optionalStartTime = Optional.ofNullable(startTime);
        Optional<Duration> optionalDuration = Optional.ofNullable(duration);
        LocalDateTime startTime = optionalStartTime.orElse(null);
//        Duration duration = optionalDuration.map(Duration::toMinutes).orElse(null);
        Duration duration = optionalDuration.orElse(null);
        // Выше защита от NPE, есть возможность попадания в toString() epic без subtasks, его значения time and duration пустые,
        // в итоге методы форматирование значений времени и duration инициируют NPE.
        return String.format(
                "Task{id=%d, name='%s', description='%s', status=%s, startTime=%s, duration=%s}",
                id,
                name,
                description,
                status,
                startTime,
                duration
        );
    }

    @Override
    public int compareTo(Task o) {
        if (o.startTime != null) {
            if (this.startTime.isEqual(o.startTime)) {
                return 0;
            } else if (this.startTime.isBefore(o.startTime)) {
                return -1;
            } else {
                return 1;
            }
        } else {
            return -1;
        }
    }
}
