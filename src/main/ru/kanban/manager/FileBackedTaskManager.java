package ru.kanban.manager;

import org.junit.jupiter.api.Assertions;
import ru.kanban.entity.Epic;
import ru.kanban.entity.Subtask;
import ru.kanban.entity.Task;
import ru.kanban.entity.TaskStatus;
import ru.kanban.exception.ManagerSaveException;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;

public class FileBackedTaskManager extends InMemoryTaskManager implements TaskManager {

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
            throw new RuntimeException(e);
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
        int taskId = Integer.parseInt(values[0]);
        String className = values[1];
        String taskName = values[2];
        TaskStatus taskStatus = getTaskStatusFromString(values[3]);
        String taskDescription = values[4];
        Epic epic = null;
        if (className.equals("SUBTASK")) {
            epic = getEpicById(Integer.parseInt(values[5].trim()));
        }

        switch (className) {
            case "TASK" -> {
                return new Task(taskName, taskDescription, taskId, taskStatus);
            }
            case "SUBTASK" -> {
                return new Subtask(taskName, taskDescription, taskId, epic, taskStatus);
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

        if (task instanceof Subtask subtask) {
            sb.append(subtask.getEpic().getId());
        }

        return sb.toString();
    }

    @Override
    public ArrayList<Task> getTasks() {
        return super.getTasks();
    }

    @Override
    public ArrayList<Subtask> getSubtasks() {
        return super.getSubtasks();
    }

    @Override
    public ArrayList<Epic> getEpics() {
        return super.getEpics();
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
    public Task getTaskById(int id) {
        return super.getTaskById(id);
    }

    @Override
    public Epic getEpicById(int id) {
        return super.getEpicById(id);
    }

    @Override
    public Subtask getSubtaskById(int id) {
        return super.getSubtaskById(id);
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

    @Override
    public int getNextId() {
        return super.getNextId();
    }

    @Override
    public ArrayList<Subtask> getSubtasksFromEpic(Epic epic) {
        return super.getSubtasksFromEpic(epic);
    }

    @Override
    public HistoryManager getHistoryManager() {
        return super.getHistoryManager();
    }

    private TaskStatus getTaskStatusFromString(String str) {
        return switch (str) {
            case "NEW" -> TaskStatus.NEW;
            case "IN_PROGRESS" -> TaskStatus.IN_PROGRESS;
            case "DONE" -> TaskStatus.DONE;
            default -> throw new IllegalArgumentException("Указан неизвестный статус: " + str);
        };
    }

    // Доп. задание - пользовательский сценарий.

    /**
     * Дополнительное задание. Реализуем пользовательский сценарий
     *
     * <p>
     * Если у вас останется время, вы можете выполнить дополнительное задание.
     * <p>
     * Создайте метод static void main(String[] args) в классе FileBackedTaskManager и реализуйте небольшой сценарий:
     * Заведите несколько разных задач, эпиков и подзадач.
     * Создайте новый FileBackedTaskManager-менеджер из этого же файла.
     * Проверьте, что все задачи, эпики, подзадачи, которые были в старом менеджере, есть в новом.
     * Обратите внимание, что выполнение этого задания необязательно.
     *
     * <p>
     * Содержимое файла:
     *
     * <p>
     * 1,TASK,Task1,NEW,Description task1 test test test,
     * <p>
     * 2,TASK,Task2,IN_PROGRESS,Some description blablabla,
     * <p>
     * 3,EPIC,Epic1,IN_PROGRESS,Epic with two Subtasks,
     * <p>
     * 4,EPIC,Epic2,DONE,Epic with one Subtask,
     * <p>
     * 5,SUBTASK,Sub Task1,DONE,Description sub task1,3
     * <p>
     * 6,SUBTASK,Sub Task2,IN_PROGRESS,Description sub task2,3
     * <p>
     * 7,SUBTASK,Sub Task3,DONE,Description sub task3,4
     */
    public static void main(String[] args) {
        FileBackedTaskManager taskManager = FileBackedTaskManager.loadFromFile(new File("src/main/resources/autosave.csv"));
        Assertions.assertEquals(2, taskManager.getTasks().size());
        Assertions.assertEquals(2, taskManager.getEpics().size());
        Assertions.assertEquals(3, taskManager.getSubtasks().size());
        Assertions.assertEquals(2, taskManager.getEpicById(3).getSubtaskArrayList().size());
    }
}