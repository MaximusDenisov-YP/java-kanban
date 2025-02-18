package ru.kanban.entity;

public class Subtask extends Task {
    private Epic epic;

    public Subtask(String name, String description, Epic epic) {
        super(name, description, TaskStatus.NEW);
        this.epic = epic;
    }

    public Subtask(String name, String description, int id, Epic epic) {
        super(name, description, id, TaskStatus.NEW);
        this.epic = epic;
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
