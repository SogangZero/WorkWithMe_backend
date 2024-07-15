package com.wwme.wwme.log.domain.DTO.tag;

import com.wwme.wwme.group.domain.Group;
import com.wwme.wwme.log.domain.DTO.EventDTO;
import com.wwme.wwme.log.domain.OperationType;
import com.wwme.wwme.task.domain.Task;
import com.wwme.wwme.user.domain.User;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;

@Getter
@Slf4j
public class CreateTagLogDTO extends EventDTO {

    String createTagName;


    @Builder(builderMethodName = "buildWithOperationStringID",
            builderClassName = "CreateTagOperationStringID")
    public CreateTagLogDTO(Long id, User user, Group group, OperationType operationTypeEnum,
                           LocalDateTime operationTime, String operationString, Task task,
                           String currentTaskName, String createTagName) {
        super(id, user, group, operationTypeEnum, operationTime, operationString, task, currentTaskName);
        this.createTagName = createTagName;
        this.setSpecificFields();
        this.isTaskLog = false;
    }

    @Builder(builderMethodName = "buildWithSpecificParamsNoID",
            builderClassName = "CreateTagSpecificParamsNoID")
    public CreateTagLogDTO(User user, Group group, OperationType operationTypeEnum,
                           LocalDateTime operationTime, Task task,
                           String createTagName) {
        super(user, group, operationTypeEnum, operationTime, task);
        this.createTagName = createTagName;
        setOperationStr();
        log.info("Create Tag Log DTO's enum : " + this.getOperationTypeEnum());
        this.isTaskLog = false;

    }

    @Override
    public void setOperationStr() {
        this.operationString = this.getCreateTagName();
    }

    @Override
    public void setSpecificFields() {
        this.createTagName = this.getOperationString();
    }

    @Override
    public String convertToString() {
        return String.format("\"%s\" 님이 \"%s\" 태그를 생성하였습니다.",
                this.getUser().getNickname(),
                this.getCreateTagName());
    }
}
