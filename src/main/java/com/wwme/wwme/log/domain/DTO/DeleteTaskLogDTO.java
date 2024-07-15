package com.wwme.wwme.log.domain.DTO;

import com.wwme.wwme.group.domain.Group;
import com.wwme.wwme.log.domain.OperationType;
import com.wwme.wwme.task.domain.Task;
import com.wwme.wwme.user.domain.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@Getter
public class DeleteTaskLogDTO extends EventDTO{

    private String deletedTaskName;

    @Builder(builderMethodName = "buildWithSpecificParams",
    builderClassName = "DeleteTaskSpecificParamsNoID")
    public DeleteTaskLogDTO(User user, Group group, OperationType operationTypeEnum, LocalDateTime operationTime, String deletedTaskName, Task task) {
        super(user, group, operationTypeEnum, operationTime,task);
        this.deletedTaskName = deletedTaskName;
        this.currentTaskName = deletedTaskName;
        setOperationStr();
        this.isTaskLog = true;
    }

    @Builder(builderMethodName = "buildWithOperationStringID",
    builderClassName = "DeleteTaskOperationStringID")
    public DeleteTaskLogDTO(Long id, User user, Group group,
                            OperationType operationTypeEnum,
                            LocalDateTime operationTime, String operationString,
                            String deletedTaskName, Task task, String currentTaskName) {
        super(id, user, group, operationTypeEnum, operationTime, operationString,task, currentTaskName);
        this.deletedTaskName = deletedTaskName;
        setSpecificFields();
        this.isTaskLog = true;
    }

    @Override
    public void setOperationStr() {
        this.operationString = deletedTaskName;
    }

    @Override
    public void setSpecificFields() {
        this.deletedTaskName = operationString;
    }

    @Override
    public String convertToString() {
        return "\"" + this.getUser().getNickname() + "\" 님이 " + "\"" + this.getCurrentTaskName() +"\""
                + "과제를 삭제하였습니다.";
    }
}
