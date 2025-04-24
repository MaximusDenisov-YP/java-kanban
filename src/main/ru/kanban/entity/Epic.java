package ru.kanban.entity;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class Epic extends Task {
    private final ArrayList<Subtask> subtaskArrayList;
    private LocalDateTime endTime = null;

    public Epic(String name, String description) {
        super(name, description, TaskStatus.NEW);
        this.subtaskArrayList = new ArrayList<>();
    }

    public Epic(String name, String description, int id) {
        super(
                name,
                description,
                id,
                TaskStatus.NEW
        );
        this.subtaskArrayList = new ArrayList<>();
    }

    public Epic(Epic epic) {
        super(
                epic.getName(),
                epic.getDescription(),
                epic.getId(),
                epic.getStatus(),
                epic.getStartTime(),
                epic.getDuration()
        );
        this.endTime = epic.endTime;
        this.subtaskArrayList = new ArrayList<>();
    }

    public ArrayList<Subtask> getSubtaskArrayList() {
        return subtaskArrayList;
    }

    public Subtask putSubtaskToEpic(Subtask subTask) {
        subtaskArrayList.add(subTask);
        this.checkEpicStatus();
        return subTask;
    }

    public void changeStartTimeAndDuration() {
        ArrayList<Subtask> subtasks = this.subtaskArrayList;
        if (this.getStartTime() == null) {
            this.setStartTime(subtasks.get(0).getStartTime());
            this.setDuration(subtasks.get(0).getDuration());
        } else {
            this.setStartTime(subtasks.stream()
                    .map(Subtask::getStartTime)
                    .min(LocalDateTime::compareTo)
                    .orElse(null)
            );
            this.setEndTime(subtasks.stream()
                    .map(Subtask::getEndTime)
                    .max(LocalDateTime::compareTo)
                    .orElse(null)
            );
            if (this.getStartTime() != null && this.getEndTime() != null) {
                this.setDuration(Duration.between(this.getStartTime(), this.getEndTime()));
            } else {
                this.setDuration(null);
            }
        }
    }

    public void checkEpicStatus() {
        int newStatusCount = 0;
        // Тут будет логика наложения отрезков.
        for (Subtask subtask : this.getSubtaskArrayList()) {
            if (subtask.getStatus() == TaskStatus.IN_PROGRESS) {
                this.setStatus(TaskStatus.IN_PROGRESS);
                return;
            }
            if (subtask.getStatus() == TaskStatus.NEW) {
                newStatusCount++;
            }
        }
        if (newStatusCount == this.getSubtaskArrayList().size()) { // Если все NEW или Subtasks нет, Статус = NEW
            this.setStatus(TaskStatus.NEW);
        } else if (newStatusCount == 0) { // Если нет ни одного NEW и в самом начале метода не произошёл return, значит все DONE.
            this.setStatus(TaskStatus.DONE);
        } else { // Если не все NEW и не все DONE, следовательно Epic в прогрессе. (В случае, если нет Subtask - IN_PROGRESS, но есть и NEW, и DONE.)
            this.setStatus(TaskStatus.IN_PROGRESS);
        }
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public void clearSubtasks() {
        this.subtaskArrayList.clear();
    }

    @Override
    public String toString() {
        return "Epic" + super.toString().substring(4);
    }

}