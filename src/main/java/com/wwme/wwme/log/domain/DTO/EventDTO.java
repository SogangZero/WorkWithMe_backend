package com.wwme.wwme.log.domain.DTO;

import com.wwme.wwme.group.domain.Group;
import com.wwme.wwme.log.domain.OperationType;
import com.wwme.wwme.user.domain.User;
import lombok.*;

import java.time.LocalDateTime;

@Getter
public abstract class EventDTO {


    public EventDTO(Long id, User user, Group group, OperationType operationTypeEnum, LocalDateTime operationTime, String operationString) {
        this.id = id;
        this.user = user;
        this.group = group;
        this.operationTypeEnum = operationTypeEnum;
        this.operationTime = operationTime;
        this.operationString = operationString;
    }


    public EventDTO(User user, Group group, OperationType operationTypeEnum, LocalDateTime operationTime) {
        this.user = user;
        this.group = group;
        this.operationTypeEnum = operationTypeEnum;
        this.operationTime = operationTime;
    }

    private Long id;
    private User user;
    private Group group;
    private OperationType operationTypeEnum;
    private LocalDateTime operationTime;
    protected String operationString;



    //받은 정보를 바탕으로 operationString 을 계산한다.
    public abstract void setOperationStr();

    public abstract void setSpecificFields();
}
