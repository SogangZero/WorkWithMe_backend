package com.wwme.wwme.log.domain.DTO;

import com.wwme.wwme.group.domain.Group;
import com.wwme.wwme.log.domain.OperationType;
import com.wwme.wwme.user.domain.User;
import lombok.*;

import java.time.LocalDateTime;

@Getter
public class CreateTaskLogDTO extends EventDTO{
    public String newTaskName;

    @Builder(builderMethodName = "buildFromService")
    public CreateTaskLogDTO(User user, Group group, OperationType operationTypeEnum, LocalDateTime operationTime, String newTaskName) {
        super(user, group, operationTypeEnum, operationTime);
        this.newTaskName = newTaskName;
    }

    @Builder(builderMethodName = "buildFromDB")
    public CreateTaskLogDTO(Long id, User user, Group group, OperationType operationTypeEnum, LocalDateTime operationTime, String operationString) {
        super(id, user, group, operationTypeEnum, operationTime, operationString);
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


}
