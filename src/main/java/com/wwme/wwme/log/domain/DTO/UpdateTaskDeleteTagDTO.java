package com.wwme.wwme.log.domain.DTO;

import com.wwme.wwme.group.domain.Group;
import com.wwme.wwme.log.domain.OperationType;
import com.wwme.wwme.task.domain.Tag;
import com.wwme.wwme.user.domain.User;
import lombok.Builder;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;


@Getter
public class UpdateTaskDeleteTagDTO extends EventDTO{
    private Tag deletedTag;
    @Builder(builderMethodName = "buildWithSpecificParamsNoID")
    public UpdateTaskDeleteTagDTO(User user, Group group, OperationType operationTypeEnum, LocalDateTime operationTime, Tag deletedTag) {
        super(user, group, operationTypeEnum, operationTime);
        this.deletedTag = deletedTag;
        setOperationStr();
    }

    @Builder(builderMethodName = "buildWithOperationStringID")
    public UpdateTaskDeleteTagDTO(Long id, User user, Group group, OperationType operationTypeEnum, LocalDateTime operationTime, String operationString) {
        super(id, user, group, operationTypeEnum, operationTime, operationString);
        setSpecificFields();
    }

    @Override
    public void setOperationStr() {
        this.operationString = deletedTag.getTagName();
    }

    @Override
    public void setSpecificFields() {
        //TODO: how do you want to set tag?
    }


}
