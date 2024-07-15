package com.wwme.wwme.log.domain.DTO;

import com.wwme.wwme.group.domain.Group;
import com.wwme.wwme.log.domain.OperationType;
import com.wwme.wwme.task.domain.Task;
import com.wwme.wwme.user.domain.User;
import lombok.Builder;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@Getter
public class UpdateTaskDueDateLogDTO extends EventDTO{

    LocalDateTime previousDueDate;
    LocalDateTime updatedDueDate;

    @Builder(builderMethodName = "buildWithSpecificParamsNoID"
    ,builderClassName = "UpdateTaskDueDateSpecificParamsNoId")
    public UpdateTaskDueDateLogDTO(User user, Group group, OperationType operationTypeEnum, LocalDateTime operationTime, LocalDateTime previousDueDate, LocalDateTime updatedDueDate, Task task) {
        super(user, group, operationTypeEnum, operationTime,task);
        this.previousDueDate = previousDueDate;
        this.updatedDueDate = updatedDueDate;
        this.currentTaskName = task.getTaskName();
        setOperationStr();
        this.isTaskLog = true;
    }

    @Builder(builderMethodName = "buildWithOperationStringID",
    builderClassName = "UpdateTaskDueDateOperationStringNoID")
    public UpdateTaskDueDateLogDTO(Long id, User user,
                                   Group group, OperationType operationTypeEnum,
                                   LocalDateTime operationTime, String operationString,
                                   Task task, String currentTaskName) {
        super(id, user, group, operationTypeEnum, operationTime, operationString,task, currentTaskName);
        setSpecificFields();
        this.isTaskLog = true;
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

    @Override
    public String convertToString() {
        return String.format("\"%s\" 님께서 과제 \"%s\" 의 마감일을 \"%s\" 에서 \"%s\" 로 변경하였습니다.",
                this.getUser().getNickname(),
                this.getCurrentTaskName(),
                this.getPreviousDueDate().toLocalDate().toString(),
                this.getUpdatedDueDate().toLocalDate().toString());
    }
}
