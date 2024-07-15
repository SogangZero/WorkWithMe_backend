package com.wwme.wwme.log.domain;

import lombok.Getter;

@Getter
public enum OperationType {
    CREATE_TASK("CreateTask"),
    DELETE_TASK("DeleteTask"),
    UPDATE_TASK_NAME("UpdateTaskName"),
    UPDATE_TASK_TYPE("UpdateTaskType"),
    UPDATE_TASK_DUE_DATE("UpdateTaskDueDate"),
    UPDATE_TASK_CHANGE_TAG("UpdateTaskChangeTag"),
    UPDATE_TASK_DELETE_TAG("UpdateTaskDeleteTag");

    private final String logStr;

    private OperationType(String logStr) {
        this.logStr = logStr;
    }

}
