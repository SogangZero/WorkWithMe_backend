package com.wwme.wwme.log.domain.DTO;

import com.wwme.wwme.group.domain.Group;
import com.wwme.wwme.log.domain.OperationType;
import com.wwme.wwme.task.domain.Task;
import com.wwme.wwme.user.domain.User;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@Getter
public class CreateTaskLogDTO extends EventDTO{
    private String newTaskName;

    @Builder(builderMethodName = "buildWithSpecificParamsNoID")
    public CreateTaskLogDTO(User user, Group group, OperationType operationTypeEnum, LocalDateTime operationTime, String newTaskName, Task task) {
        super(user, group, operationTypeEnum, operationTime, task);
        this.newTaskName = newTaskName;
        setOperationStr();
    }

    @Builder(builderMethodName = "buildWithOperationStringID")
    public CreateTaskLogDTO(Long id, User user, Group group, OperationType operationTypeEnum, LocalDateTime operationTime, String operationString, Task task) {
        super(id, user, group, operationTypeEnum, operationTime, operationString, task);
        setSpecificFields();
    }





    @Override
    public void setOperationStr() {
        this.operationString = newTaskName;
    }

    @Override
    public void setSpecificFields() {
        this.newTaskName = operationString;
    }

    @Override
    public String convertToString() {
        return "\"" + this.getUser().getNickname() + "\" 님이 " + "\"" + this.getNewTaskName() +"\""
                + "과제를 생성하였습니다.";
    }


}
