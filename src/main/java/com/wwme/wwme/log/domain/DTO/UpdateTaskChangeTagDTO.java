package com.wwme.wwme.log.domain.DTO;

import com.wwme.wwme.group.domain.Group;
import com.wwme.wwme.log.domain.OperationType;
import com.wwme.wwme.task.domain.Tag;
import com.wwme.wwme.task.domain.Task;
import com.wwme.wwme.user.domain.User;
import lombok.Builder;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@Getter
public class UpdateTaskChangeTagDTO extends EventDTO{

    private String previousTagName;
    private String updateTagName;

    @Builder(builderMethodName = "buildWithSpecificParamsNoID",
    builderClassName = "UpdateTaskChangeTagSpecificParamsNoID")
    public UpdateTaskChangeTagDTO(User user, Group group, OperationType operationTypeEnum, LocalDateTime operationTime, String previousTagName, String updateTagName, Task task) {
        super(user, group, operationTypeEnum, operationTime, task);
        this.previousTagName = previousTagName;
        this.updateTagName = updateTagName;
        setOperationStr();
    }

    @Builder(builderMethodName = "buildWithOperationStringID",
    builderClassName = "UpdateTaskChangeTagOperationStringID")
    public UpdateTaskChangeTagDTO(Long id, User user, Group group, OperationType operationTypeEnum, LocalDateTime operationTime, String operationString, Task task) {
        super(id, user, group, operationTypeEnum, operationTime, operationString,task);
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
        return String.format("\"%s\" 님께서과제 \"%s\" 의 Tag 를 \"%s\" 에서 \"%s\" 로 변경하였습니다.",
                this.getUser().getNickname(),
                this.getTask().getTaskName(),
                this.getPreviousTagName(),
                this.getUpdateTagName());
    }


}
