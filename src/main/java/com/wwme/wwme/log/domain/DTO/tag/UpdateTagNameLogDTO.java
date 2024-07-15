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
public class UpdateTagNameLogDTO extends EventDTO {
    String prevTagName;
    String changedTagName;

    //BuildFromDB
    @Builder(builderMethodName = "buildWithOperationStringID",
            builderClassName = "UpdateTagNameOperationStringID")
    public UpdateTagNameLogDTO(Long id, User user, Group group, OperationType operationTypeEnum,
                               LocalDateTime operationTime, String operationString,
                               Task task, String currentTaskName) {
        super(id, user, group, operationTypeEnum, operationTime, operationString, task, currentTaskName);
        this.setSpecificFields();
        this.isTaskLog = false;
    }


    //Build From Input
    @Builder(builderMethodName = "buildWithSpecificParamsNoID",
            builderClassName = "CreateTaskSpecificParamsNoID")
    public UpdateTagNameLogDTO(User user, Group group, OperationType operationTypeEnum,
                               LocalDateTime operationTime, Task task,
                               String prevTagName, String changedTagName) {
        super(user, group, operationTypeEnum, operationTime, task);
        this.prevTagName = prevTagName;
        this.changedTagName = changedTagName;
        this.setOperationStr();
        this.isTaskLog = false;
    }

    @Override
    public void setOperationStr() {
        this.operationString = this.getPrevTagName() + "|" + this.getChangedTagName();
    }

    @Override
    public void setSpecificFields() {
        String[] operArr = this.operationString.split("\\|");
        this.prevTagName =  operArr[0];
        this.changedTagName = operArr[1];
    }

    @Override
    public String convertToString() {
        return String.format("\"%s\" 님이 \"%s\" 태그의 이름을 \"%s\" 로 변경하였습니다.",
                this.getUser().getNickname(),
                this.getPrevTagName(),
                this.getChangedTagName());
    }
}
