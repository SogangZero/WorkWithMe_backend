package com.wwme.wwme.log.domain.DTO.tag;

import com.wwme.wwme.group.domain.Group;
import com.wwme.wwme.log.domain.DTO.EventDTO;
import com.wwme.wwme.log.domain.OperationType;
import com.wwme.wwme.task.domain.Task;
import com.wwme.wwme.user.domain.User;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;


@Getter
public class DeleteTagLogDTO extends EventDTO {
    String deletedTagName;


    @Builder(builderMethodName = "buildWithOperationStringID",
            builderClassName = "UpdateTagNameOperationStringID")
    public DeleteTagLogDTO(Long id, User user, Group group, OperationType operationTypeEnum,
                           LocalDateTime operationTime, String operationString, Task task,
                           String currentTaskName) {
        super(id, user, group, operationTypeEnum, operationTime, operationString, task, currentTaskName);
        setSpecificFields();
        this.isTaskLog = false;
    }

    @Builder(builderMethodName = "buildWithSpecificParamsNoID",
            builderClassName = "CreateTaskSpecificParamsNoID")
    public DeleteTagLogDTO(User user, Group group, OperationType operationTypeEnum,
                           LocalDateTime operationTime, Task task, String deletedTagName) {
        super(user, group, operationTypeEnum, operationTime, task);
        this.deletedTagName = deletedTagName;
        setOperationStr();
        this.isTaskLog = false;
    }

    @Override
    public void setOperationStr() {
        this.operationString = this.getDeletedTagName();
    }

    @Override
    public void setSpecificFields() {
        this.deletedTagName = this.getOperationString();
    }

    @Override
    public String convertToString() {
        return String.format("\"%s\" 님이 \"%s\" 태그를 삭제하였습니다.",
                this.getUser().getNickname(),
                this.getDeletedTagName());
    }
}
