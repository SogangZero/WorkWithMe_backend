package com.wwme.wwme.log.domain.DTO;

import com.wwme.wwme.group.domain.Group;
import com.wwme.wwme.log.domain.OperationType;
import com.wwme.wwme.task.domain.Task;
import com.wwme.wwme.user.domain.User;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@Getter
@Setter
public abstract class EventDTO {

    public EventDTO(Long id, User user, Group group, OperationType operationTypeEnum, LocalDateTime operationTime, String operationString, Task task) {
        this.id = id;
        this.user = user;
        this.group = group;
        this.operationTypeEnum = operationTypeEnum;
        this.operationTime = operationTime;
        this.operationString = operationString;
        this.task = task;
    }

    public EventDTO(User user, Group group, OperationType operationTypeEnum, LocalDateTime operationTime, Task task) {
        this.user = user;
        this.group = group;
        this.operationTypeEnum = operationTypeEnum;
        this.operationTime = operationTime;
        this.task = task;
    }

    protected Long id;
    protected User user;
    protected Group group;
    protected Task task;
    protected OperationType operationTypeEnum;
    protected LocalDateTime operationTime;
    protected String operationString;


    //받은 정보를 바탕으로 operationString 을 계산한다.
    public abstract void setOperationStr();

    @Override
    public String toString() {
        return "EventDTO{" +
                "id=" + id +
                ", user=" + user.getNickname() +
                ", group=" + group.getGroupName() +
                ", task=" + task.getTaskName() +
                ", operationTypeEnum=" + operationTypeEnum +
                ", operationTime=" + operationTime +
                ", operationString='" + operationString + '\'' +
                '}';
    }

    public abstract void setSpecificFields();
    public abstract String convertToString();
}
