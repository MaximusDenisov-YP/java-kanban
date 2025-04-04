package ru.kanban.manager;

import ru.kanban.entity.Task;

import java.util.*;

public class InMemoryHistoryManager implements HistoryManager {

    public final LinkedHistoryList historyList = new LinkedHistoryList();

    @Override
    public void add(Task task) {
        historyList.linkLast(new Task(task));
    }

    @Override
    public List<Task> getHistory() {
        return historyList.getTasks();
    }

    @Override
    public void remove(int id) {
        historyList.removeNode(id);
    }

    static class LinkedHistoryList {

        Map<Integer, Node> historyMap = new HashMap<>();
        public int size = 0;

        Node first;
        Node last;

        void linkLast(Task task) {
            removeNode(task.getId());
            final Node l = last;
            final Node newNode = new Node(l, task, null);
            last = newNode;
            if (l == null) {
                first = newNode;
            } else {
                l.next = newNode;
            }
            historyMap.put(task.getId(), newNode);
            size++;
        }

        ArrayList<Task> getTasks() {
            ArrayList<Task> result = new ArrayList<>();
            for (Node node : historyMap.values()) {
                result.add(node.item);
            }
            return result;
        }

        public void removeNode(int index) {
            Node currentNode = historyMap.get(index);
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
            historyMap.remove(index);
            size--;
        }
    }

}