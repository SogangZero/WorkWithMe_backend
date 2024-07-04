package com.wwme.wwme.log.domain;

import lombok.Getter;

@Getter
public enum OperationType {
    CREATE_TASK("생성"),
    DELETE_TASK("삭제"),
    UPDATE_TASK_NAME("과제 이름"),
    UPDATE_TASK_TYPE("과제 종류"),
    UPDATE_TASK_DUE_DATE("과제 마감일"),
    UPDATE_TASK_CHANGE_TAG("과제 태그"),
    UPDATE_TASK_DELETE_TAG("과제 태그");

    private final String logStr;

    private OperationType(String logStr) {
        this.logStr = logStr;
    }

}
