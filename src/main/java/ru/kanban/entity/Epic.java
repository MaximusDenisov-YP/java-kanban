package main.java.ru.kanban.entity;

import java.util.ArrayList;

public class Epic extends Task {
    private final ArrayList<SubTask> subTaskArrayList;

    public Epic(String name, String description) {
        super(name, description, TaskStatus.NEW);
        this.subTaskArrayList = new ArrayList<>();
    }

    public Epic(String name, String description, int id) {
        super(name, description, id, TaskStatus.NEW);
        this.subTaskArrayList = new ArrayList<>();
    }

    public ArrayList<SubTask> getSubTaskArrayList() {
        return subTaskArrayList;
    }

    public void putSubTaskToEpic(SubTask subTask) {
        subTaskArrayList.add(subTask);
        this.checkEpicStatus();
    }

    public void checkEpicStatus() {
        int newStatusCount = 0;
        for (SubTask subtask : this.getSubTaskArrayList()) {
            if (subtask.getStatus() == TaskStatus.IN_PROGRESS) {
                this.setStatus(TaskStatus.IN_PROGRESS);
                return;
            }
            if (subtask.getStatus() == TaskStatus.NEW) {
                newStatusCount++;
            }
        }
        if (newStatusCount == this.getSubTaskArrayList().size()) { // Если все NEW или SubTasks нет, Статус = NEW
            this.setStatus(TaskStatus.NEW);
        } else if (newStatusCount == 0) { // Если нет ни одного NEW и в самом начале метода не произошёл return, значит все DONE.
            this.setStatus(TaskStatus.DONE);
        } else { // Если не все NEW и не все DONE, следовательно Epic в прогрессе. (В случае, если нет SubTask - IN_PROGRESS, но есть и NEW, и DONE.)
            this.setStatus(TaskStatus.IN_PROGRESS);
        }
    }

    public void clearSubTasks() {
        this.subTaskArrayList.clear();
    }

    @Override
    public String toString() {
        return "Epic"+ super.toString().substring(4);
    }

}