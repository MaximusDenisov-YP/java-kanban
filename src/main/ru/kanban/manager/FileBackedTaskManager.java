package ru.kanban.manager;

import ru.kanban.entity.Epic;
import ru.kanban.entity.Subtask;
import ru.kanban.entity.Task;
import ru.kanban.entity.TaskStatus;
import ru.kanban.exception.ManagerSaveException;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;

public class FileBackedTaskManager extends InMemoryTaskManager {

    private File autoSave;

    public FileBackedTaskManager(File file) {
        this.autoSave = file;
    }

    public FileBackedTaskManager() {
    }

    public File getAutosave() {
        return this.autoSave;
    }

    public static FileBackedTaskManager loadFromFile(File file) {
        FileBackedTaskManager taskManager = new FileBackedTaskManager(file);
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            while (br.ready()) {
                String line = br.readLine();
                if (line.isBlank()) continue;
                Task someTask = taskManager.fromString(line);
                switch (someTask.getClass().getSimpleName()) {
                    case "Task" -> taskManager.postTask(someTask);
                    case "Epic" -> taskManager.postEpic((Epic) someTask);
                    case "Subtask" -> taskManager.postSubtask((Subtask) someTask);
                }
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка при чтении файла -> " + e.getMessage());
        }
        return taskManager;
    }

    public void save() {
        StringBuilder sb = new StringBuilder();
        this.getTasks().forEach(task -> sb.append(this.toString(task)).append("\n"));
        this.getEpics().forEach(epic -> sb.append(this.toString(epic)).append("\n"));
        this.getSubtasks().forEach(subtask -> sb.append(this.toString(subtask)).append("\n"));
        try {
            Files.writeString(autoSave.toPath(), sb, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException exc) {
            throw new ManagerSaveException("Произошла ошибка записи сейва в файл: \n" + sb);
        }
    }

    public Task fromString(String value) {
        String[] values = value.split(",");
        if (values.length < 7) {
            throw new IllegalArgumentException("Недостаточно полей в строке: " + value);
        }
        int taskId = Integer.parseInt(values[0]);
        String className = values[1];
        String taskName = values[2];
        TaskStatus taskStatus = getTaskStatusFromString(values[3]);
        String taskDescription = values[4];
        LocalDateTime dateTime = LocalDateTime.parse(values[5]);
        Duration duration = Duration.ofMinutes(Long.parseLong(values[6]));
        Epic epic = null;
        if (className.equals("SUBTASK")) {
            dateTime = LocalDateTime.parse(values[5]);
            duration = Duration.ofMinutes(Long.parseLong(values[6]));
            epic = getEpicById(Integer.parseInt(values[7].trim())).orElseThrow();
        }
        switch (className) {
            case "TASK" -> {
                return new Task(taskName, taskDescription, taskId, taskStatus, dateTime, duration);
            }
            case "SUBTASK" -> {
                return new Subtask(taskName, taskDescription, taskId, epic, taskStatus, dateTime, duration);
            }
            case "EPIC" -> {
                return new Epic(taskName, taskDescription, taskId);
            }
            default -> throw new RuntimeException("На вход поступил неизвестный тип задачи -> " + className);
        }
    }

    public String toString(Task task) {
        StringBuilder sb = new StringBuilder();
        sb.append(task.getId()).append(",");
        sb.append(task.getClass().getSimpleName().toUpperCase()).append(",");
        sb.append(task.getName()).append(",");
        sb.append(task.getStatus()).append(",");
        sb.append(task.getDescription()).append(",");
        sb.append(task.getStartTime()).append(",");
        Optional<Duration> optionalDuration = Optional.ofNullable(task.getDuration());
        if (optionalDuration.isPresent()) sb.append(task.getDuration().toMinutes()).append(",");
        else sb.append("null").append(",");

        if (task instanceof Subtask subtask) {
            sb.append(subtask.getEpic().getId());
        }

        return sb.toString();
    }

    @Override
    public void deleteAllTasks() {
        super.deleteAllTasks();
        save();
    }

    @Override
    public void deleteAllSubtasks() {
        super.deleteAllSubtasks();
        save();
    }

    @Override
    public void deleteAllEpics() {
        super.deleteAllEpics();
        save();
    }

    @Override
    public Task postTask(Task task) {
        super.postTask(task);
        save();
        return new Task(task);
    }

    @Override
    public Epic postEpic(Epic epic) {
        super.postEpic(epic);
        save();
        return new Epic(epic);
    }

    @Override
    public Subtask postSubtask(Subtask subTask) {
        super.postSubtask(subTask);
        save();
        return new Subtask(subTask);
    }

    @Override
    public Task updateTask(Task task) {
        Task resultTask = super.updateTask(task);
        save();
        return new Task(resultTask);
    }

    @Override
    public Epic updateEpic(Epic epic) {
        Epic resultEpic = super.updateEpic(epic);
        save();
        return new Epic(resultEpic);
    }

    @Override
    public Subtask updateSubtask(Subtask subTask) {
        Subtask resultSubtask = super.updateSubtask(subTask);
        save();
        return new Subtask(resultSubtask);
    }

    @Override
    public Task deleteTaskById(int id) {
        Task resultTask = super.deleteTaskById(id);
        save();
        return new Task(resultTask);
    }

    @Override
    public Epic deleteEpicById(int id) {
        Epic resultEpic = super.deleteEpicById(id);
        save();
        return new Epic(resultEpic);
    }

    @Override
    public Subtask deleteSubtaskById(int id) {
        Subtask resultSubtask = super.deleteSubtaskById(id);
        save();
        return new Subtask(resultSubtask);
    }

    private TaskStatus getTaskStatusFromString(String str) {
        return switch (str) {
            case "NEW" -> TaskStatus.NEW;
            case "IN_PROGRESS" -> TaskStatus.IN_PROGRESS;
            case "DONE" -> TaskStatus.DONE;
            default -> throw new IllegalArgumentException("Указан неизвестный статус: " + str);
        };
    }

}
