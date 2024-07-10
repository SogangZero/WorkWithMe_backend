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
public class UpdateTaskChangeTagDTO extends EventDTO{

    private String previousTagName;
    private String updateTagName;

    @Builder(builderMethodName = "buildWithSpecificParamsNoID")
    public UpdateTaskChangeTagDTO(User user, Group group, OperationType operationTypeEnum, LocalDateTime operationTime, String previousTagName, String updateTagName) {
        super(user, group, operationTypeEnum, operationTime);
        this.previousTagName = previousTagName;
        this.updateTagName = updateTagName;
        setOperationStr();
    }

    @Builder(builderMethodName = "buildWithOperationStringID")
    public UpdateTaskChangeTagDTO(Long id, User user, Group group, OperationType operationTypeEnum, LocalDateTime operationTime, String operationString) {
        super(id, user, group, operationTypeEnum, operationTime, operationString);
        setSpecificFields();
    }



    @Override
    public void setOperationStr() {
         super.operationString = this.getPreviousTagName() + "|" + this.getUpdateTagName();
    }

    @Override
    public void setSpecificFields() {
        String[] strings = this.operationString.split("\\|");
        this.previousTagName = strings[0];
        this.updateTagName = strings[1];
    }

    @Override
    public String convertToString() {

    }


}
