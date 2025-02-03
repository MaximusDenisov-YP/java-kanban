package entity;

public class SubTask extends Task {
    private Epic epic;

    public SubTask(String name, String description, int id, Epic epic) {
        super(name, description, id, TaskStatus.NEW);
        this.epic = epic;
    }

    public Epic getEpic() {
        return epic;
    }

    @Override
    public String getName() {
        return super.getName();
    }

    @Override
    public void setName(String name) {
        super.setName(name);
    }

    @Override
    public TaskStatus getStatus() {
        return super.getStatus();
    }

    @Override
    public void setStatus(TaskStatus taskStatus) {
        super.setStatus(taskStatus);
        epic.checkStatus();
    }

    @Override
    public int getId() {
        return super.getId();
    }

    @Override
    public String getDescription() {
        return super.getDescription();
    }

    @Override
    public void setDescription(String description) {
        super.setDescription(description);
    }

    @Override
    public String toString() {
        return super.toString()
                .substring(0, super.toString().length() - 4)
                + "SubTask\nПривязан к Epic: " + epic.getName();
    }

}
