package ru.kanban.manager;

import ru.kanban.entity.Task;

public class Node {
    Task item;
    Node next;
    Node prev;

    Node(Node prev, Task element, Node next) {
        this.item = element;
        this.next = next;
        this.prev = prev;
    }

    @Override
    public String toString() {
        return String.valueOf(item.getId());
    }

}