package com.wwme.wwme.log.domain.DTO;

import com.wwme.wwme.group.domain.Group;
import com.wwme.wwme.log.domain.OperationType;
import com.wwme.wwme.user.domain.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@Getter
@SuperBuilder
public class UpdateTaskNameLogDTO extends EventDTO{

    private String beforeTaskName;
    private String afterTaskName;

    public UpdateTaskNameLogDTO(User user, Group group, OperationType operationTypeEnum, LocalDateTime operationTime, String beforeTaskName, String afterTaskName) {
        super(user, group, operationTypeEnum, operationTime);
        this.beforeTaskName = beforeTaskName;
        this.afterTaskName = afterTaskName;
        setOperationStr();
    }

    public UpdateTaskNameLogDTO(Long id, User user, Group group, OperationType operationTypeEnum, LocalDateTime operationTime, String operationString) {
        super(id, user, group, operationTypeEnum, operationTime, operationString);
        setSpecificFields();
    }

    @Override
    public void setOperationStr() {
        this.operationString = beforeTaskName+"|"+afterTaskName;
    }

    @Override
    public void setSpecificFields() {
        String[] strings = this.operationString.split("\\|");
        this.beforeTaskName = strings[0];
        this.afterTaskName = strings[1];
    }

}
