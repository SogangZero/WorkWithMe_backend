package com.wwme.wwme.log.domain.DTO;

import com.wwme.wwme.group.domain.Group;
import com.wwme.wwme.log.domain.OperationType;
import com.wwme.wwme.user.domain.User;
import lombok.Builder;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@Getter
public class UpdateTaskDueDateLogDTO extends EventDTO{

    LocalDateTime previousDueDate;
    LocalDateTime updatedDueDate;

    @Builder(builderMethodName = "buildWithSpecificParamsNoID")
    public UpdateTaskDueDateLogDTO(User user, Group group, OperationType operationTypeEnum, LocalDateTime operationTime, LocalDateTime previousDueDate, LocalDateTime updatedDueDate) {
        super(user, group, operationTypeEnum, operationTime);
        this.previousDueDate = previousDueDate;
        this.updatedDueDate = updatedDueDate;
        setOperationStr();
    }

    @Builder(builderMethodName = "buildWithOperationStringID")
    public UpdateTaskDueDateLogDTO(Long id, User user, Group group, OperationType operationTypeEnum, LocalDateTime operationTime, String operationString) {
        super(id, user, group, operationTypeEnum, operationTime, operationString);
        setSpecificFields();
    }

    @Override
    public void setOperationStr() {
        this.operationString = previousDueDate.toString() + "|" + updatedDueDate.toString();
    }

    @Override
    //TODO: Test how parsing happens.
    public void setSpecificFields() {
        String[] strings = this.operationString.split("\\|");
        this.previousDueDate = LocalDateTime.parse(strings[0]);
        this.updatedDueDate = LocalDateTime.parse(strings[1]);
    }
}
