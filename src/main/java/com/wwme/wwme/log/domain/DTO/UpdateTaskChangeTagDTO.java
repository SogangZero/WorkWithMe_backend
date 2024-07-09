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
@SuperBuilder
public class UpdateTaskChangeTagDTO extends EventDTO{

    private Tag previousTag;
    private Tag updatedTag;


    public UpdateTaskChangeTagDTO(User user, Group group, OperationType operationTypeEnum, LocalDateTime operationTime, Tag previousTag, Tag updatedTag) {
        super(user, group, operationTypeEnum, operationTime);
        this.previousTag = previousTag;
        this.updatedTag = updatedTag;
        setOperationStr();
    }


    public UpdateTaskChangeTagDTO(Long id, User user, Group group, OperationType operationTypeEnum, LocalDateTime operationTime, String operationString) {
        super(id, user, group, operationTypeEnum, operationTime, operationString);
        setSpecificFields();
    }



    @Override
    public void setOperationStr() {
         super.operationString = previousTag.getTagName() + "|" + updatedTag.getTagName();
    }

    @Override
    public void setSpecificFields() {
        //TODO: make DB access
    }
}
