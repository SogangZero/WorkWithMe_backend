package com.wwme.wwme.log.domain.DTO;

import com.wwme.wwme.group.domain.Group;
import com.wwme.wwme.log.domain.OperationType;
import com.wwme.wwme.task.domain.Task;
import com.wwme.wwme.user.domain.User;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@Getter
public abstract class EventDTO {

    @Builder(builderMethodName = "eventDTOBuilderWithId")
    public EventDTO(Long id, User user, Group group, OperationType operationTypeEnum, LocalDateTime operationTime, String operationString, Task task) {
        this.id = id;
        this.user = user;
        this.group = group;
        this.operationTypeEnum = operationTypeEnum;
        this.operationTime = operationTime;
        this.operationString = operationString;
        this.task = task;
    }

    @Builder(builderMethodName = "eventDTOBuilderWithoutId")
    public EventDTO(User user, Group group, OperationType operationTypeEnum, LocalDateTime operationTime, Task task) {
        this.user = user;
        this.group = group;
        this.operationTypeEnum = operationTypeEnum;
        this.operationTime = operationTime;
        this.task = task;
    }

    private Long id;
    private User user;
    private Group group;
    private Task task;
    private OperationType operationTypeEnum;
    private LocalDateTime operationTime;
    protected String operationString;



    //받은 정보를 바탕으로 operationString 을 계산한다.
    public abstract void setOperationStr();

    public abstract void setSpecificFields();
    public abstract String convertToString();
}
