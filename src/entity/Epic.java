package entity;

import java.util.ArrayList;

public class Epic extends Task {
    private final ArrayList<SubTask> subTaskArrayList;

    public Epic(String name, String description, int id) {
        super(name, description, id, TaskStatus.NEW);
        this.subTaskArrayList = new ArrayList<>();
    }

    public ArrayList<SubTask> getSubTaskArrayList() {
        return subTaskArrayList;
    }

    public void putSubTaskToEpic(SubTask subTask) {
        subTaskArrayList.add(subTask);
        setStatus(checkStatus());
    }

    public TaskStatus checkStatus() {
        boolean isNew = true;
        boolean isDone = true;

        //Проверяем все ли subTasks NEW
        for (SubTask subTask : subTaskArrayList) {
            if (!isNew) break;
            isNew = subTask.getStatus() == TaskStatus.NEW;
        }

        //Проверяем все ли subTasks DONE
        for (SubTask subTask : subTaskArrayList) {
            if (!isDone) break;
            isDone = subTask.getStatus() == TaskStatus.DONE;
        }

        if (isNew) {
            return TaskStatus.NEW;
        } else if (isDone) {
            return TaskStatus.DONE;
        } else {
            return TaskStatus.IN_PROGRESS;
        }
    }

    @Override
    public void setStatus(TaskStatus taskStatus) {
        super.setStatus(taskStatus);
    }

    @Override
    public String toString() {
        return super.toString().substring(0, super.toString().length() - 4) + "Epic";
    }

}