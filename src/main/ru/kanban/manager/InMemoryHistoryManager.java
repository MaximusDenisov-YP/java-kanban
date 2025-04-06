package ru.kanban.manager;

import ru.kanban.entity.Task;

import java.util.*;

public class InMemoryHistoryManager implements HistoryManager {

    private final Map<Integer, Node> historyMap = new HashMap<>();

    private Node first;
    private Node last;

    @Override
    public void add(Task task) {
        linkLast(new Task(task));
    }

    @Override
    public List<Task> getHistory() {
        ArrayList<Task> historyTasks = new ArrayList<>();
        Node node = first;
        while (node != null) {
            historyTasks.add(node.item);
            node = node.next;
        }
        return historyTasks;
    }

    @Override
    public void remove(int id) {
        Node currentNode = historyMap.get(id);
        if (currentNode == null) return;
        if (currentNode.prev != null) {
            currentNode.prev.next = currentNode.next;
        } else {
            first = currentNode.next;
        }
        if (currentNode.next != null) {
            currentNode.next.prev = currentNode.prev;
        } else {
            last = currentNode.prev;
        }
        historyMap.remove(id);
    }

    void linkLast(Task task) {
        remove(task.getId());
        final Node l = last;
        final Node newNode = new Node(l, task, null);
        last = newNode;
        if (l == null) {
            first = newNode;
        } else {
            l.next = newNode;
        }
        historyMap.put(task.getId(), newNode);
    }

    public static class Node {
        private Task item;
        private Node next;
        private Node prev;

        private Node(Node prev, Task element, Node next) {
            this.item = element;
            this.next = next;
            this.prev = prev;
        }

    }
}