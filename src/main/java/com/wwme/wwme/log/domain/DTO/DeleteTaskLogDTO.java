package com.wwme.wwme.log.domain.DTO;

import com.wwme.wwme.group.domain.Group;
import com.wwme.wwme.log.domain.OperationType;
import com.wwme.wwme.user.domain.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
public class DeleteTaskLogDTO extends EventDTO{

    private String deletedTaskName;

    @Builder(builderMethodName = "buildFromService")
    public DeleteTaskLogDTO(User user, Group group, OperationType operationTypeEnum, LocalDateTime operationTime, String deletedTaskName) {
        super(user, group, operationTypeEnum, operationTime);
        this.deletedTaskName = deletedTaskName;
        setOperationStr();
    }

    @Builder(builderMethodName = "buildFromDB")
    public DeleteTaskLogDTO(Long id, User user, Group group, OperationType operationTypeEnum, LocalDateTime operationTime, String operationString, String deletedTaskName) {
        super(id, user, group, operationTypeEnum, operationTime, operationString);
        this.deletedTaskName = deletedTaskName;
        setSpecificFields();
    }

    @Override
    public void setOperationStr() {
        this.operationString = deletedTaskName;
    }

    @Override
    public void setSpecificFields() {
        this.deletedTaskName = operationString;
    }
}
