package com.wwme.wwme.notification;

public enum NotificationType {
    DUE_DATE("due_date"),
    TASK_CREATION("task_creation"),
    TASK_CHANGE("task_change"),
    GROUP_ENTRANCE("group_entrance");

    private final String type;

    NotificationType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return this.type;
    }
}
