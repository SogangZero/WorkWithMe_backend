package com.wwme.wwme.log.domain.DTO;

import com.wwme.wwme.group.domain.Group;
import com.wwme.wwme.log.domain.OperationType;
import com.wwme.wwme.task.domain.Task;
import com.wwme.wwme.user.domain.User;
import lombok.*;
import lombok.experimental.SuperBuilder;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;

@Slf4j
@Getter
public class CreateTaskLogDTO extends EventDTO{
    private String newTaskName;

    @Builder(builderMethodName = "buildWithSpecificParamsNoID",
    builderClassName = "SpecificParamsNoID")
    public CreateTaskLogDTO(User user, Group group, OperationType operationTypeEnum, LocalDateTime operationTime, String newTaskName, Task task) {
        super(user, group, operationTypeEnum, operationTime, task);
        this.newTaskName = newTaskName;
        setOperationStr();
    }

    @Builder(builderMethodName = "buildWithOperationStringID",
    builderClassName = "OperationStringID") //buildWithOperationStringID
    public CreateTaskLogDTO(Long id, User user, Group group, OperationType operationTypeEnum, LocalDateTime operationTime, String operationString, Task task) {
        super(id, user, group, operationTypeEnum, operationTime, operationString, task);
        setSpecificFields();
    }

    @Override
    public String toString() {

        return super.toString() +  "CreateTaskLogDTO{" +
                "newTaskName='" + newTaskName + '\'' +
                ", operationString='" + operationString + '\'' +
                '}';
    }

    @Override
    public void setOperationStr() {
        this.operationString = newTaskName;
    }

    @Override
    public void setSpecificFields() {
        this.newTaskName = this.getOperationString();
    }

    @Override
    public String convertToString() {
        log.info(this.getUser().getNickname());
        log.info(this.getNewTaskName());

        return "\"" + this.getUser().getNickname() + "\" 님이 " + "\"" + this.getNewTaskName() +"\""
                + "과제를 생성하였습니다.";

    }


}
