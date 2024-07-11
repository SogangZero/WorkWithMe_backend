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
public class UpdateTaskNameLogDTO extends EventDTO{

    private String beforeTaskName;
    private String afterTaskName;

    @Builder(builderMethodName = "buildWithSpecificParamsNoID")
    public UpdateTaskNameLogDTO(User user, Group group, OperationType operationTypeEnum, LocalDateTime operationTime, String beforeTaskName, String afterTaskName, Task task) {
        super(user, group, operationTypeEnum, operationTime, task);
        this.beforeTaskName = beforeTaskName;
        this.afterTaskName = afterTaskName;
        setOperationStr();
    }

    @Builder(builderMethodName = "buildWithOperationStringID")
    public UpdateTaskNameLogDTO(Long id, User user, Group group, OperationType operationTypeEnum, LocalDateTime operationTime, String operationString, Task task) {
        super(id, user, group, operationTypeEnum, operationTime, operationString,task);
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

    @Override
    public String convertToString() {
        return String.format("\"%s\" 님께서 과제 \"%s\" 의 이름을 \"%s\" 에서 \"%s\" 로 변경하였습니다.",
                this.getUser().getNickname(),
                this.getTask().getTaskName(),
                this.getBeforeTaskName(),
                this.getAfterTaskName());
    }

}
