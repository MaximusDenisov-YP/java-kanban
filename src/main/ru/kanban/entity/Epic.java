package ru.kanban.entity;

import java.util.ArrayList;

public class Epic extends Task {
    private final ArrayList<Subtask> subtaskArrayList;

    public Epic(String name, String description) {
        super(name, description, TaskStatus.NEW);
        this.subtaskArrayList = new ArrayList<>();
    }

    public Epic(String name, String description, int id) {
        super(name, description, id, TaskStatus.NEW);
        this.subtaskArrayList = new ArrayList<>();
    }

    public ArrayList<Subtask> getSubtaskArrayList() {
        return subtaskArrayList;
    }

    public void putSubtaskToEpic(Subtask subTask) {
        subtaskArrayList.add(subTask);
        this.checkEpicStatus();
    }

    public void checkEpicStatus() {
        int newStatusCount = 0;
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

    public void clearSubtasks() {
        this.subtaskArrayList.clear();
    }

    @Override
    public String toString() {
        return "Epic"+ super.toString().substring(4);
    }

}