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

    @Builder(builderMethodName = "buildWithSpecificParams")
    public DeleteTaskLogDTO(User user, Group group, OperationType operationTypeEnum, LocalDateTime operationTime, String deletedTaskName, Task task) {
        super(user, group, operationTypeEnum, operationTime,task);
        this.deletedTaskName = deletedTaskName;
        setOperationStr();
    }

    @Builder(builderMethodName = "buildWithOperationStringID")
    public DeleteTaskLogDTO(Long id, User user, Group group, OperationType operationTypeEnum, LocalDateTime operationTime, String operationString, String deletedTaskName, Task task) {
        super(id, user, group, operationTypeEnum, operationTime, operationString,task);
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

    @Override
    public String convertToString() {
        return "\"" + this.getUser().getNickname() + "\" 님이 " + "\"" + this.getDeletedTaskName() +"\""
                + "과제를 삭제하였습니다.";
    }
}
