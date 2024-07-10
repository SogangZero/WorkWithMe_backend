package com.wwme.wwme.log.service;

import com.wwme.wwme.log.domain.DTO.*;
import com.wwme.wwme.log.domain.Event;

public class EventDTOFactory {
    public static EventDTO createEventDTO(Event event){
        switch (event.getOperationTypeEnum()){
            case CREATE_TASK:
                return CreateTaskLogDTO.buildWithOperationStringID()
                        .id(event.getId())
                        .user(event.getUser())
                        .operationTime(event.getOperationTime())
                        .operationString(event.getOperationString())
                        .operationTypeEnum(event.getOperationTypeEnum())
                        .group(event.getGroup())
                        .build();
            case DELETE_TASK:
                return DeleteTaskLogDTO.buildWithOperationStringID()
                        .id(event.getId())
                        .user(event.getUser())
                        .operationTime(event.getOperationTime())
                        .operationString(event.getOperationString())
                        .operationTypeEnum(event.getOperationTypeEnum())
                        .group(event.getGroup())
                        .build();
            case UPDATE_TASK_CHANGE_TAG:
                return UpdateTaskChangeTagDTO.buildWithOperationStringID()
                        .id(event.getId())
                        .user(event.getUser())
                        .operationTime(event.getOperationTime())
                        .operationString(event.getOperationString())
                        .operationTypeEnum(event.getOperationTypeEnum())
                        .group(event.getGroup())
                        .build();
            case UPDATE_TASK_DELETE_TAG:
                return UpdateTaskDeleteTagDTO.buildWithOperationStringID()
                        .id(event.getId())
                        .user(event.getUser())
                        .operationTime(event.getOperationTime())
                        .operationString(event.getOperationString())
                        .operationTypeEnum(event.getOperationTypeEnum())
                        .group(event.getGroup())
                        .build();
            case UPDATE_TASK_DUE_DATE:
                return UpdateTaskDueDateLogDTO.buildWithOperationStringID()
                        .id(event.getId())
                        .user(event.getUser())
                        .operationTime(event.getOperationTime())
                        .operationString(event.getOperationString())
                        .operationTypeEnum(event.getOperationTypeEnum())
                        .group(event.getGroup())
                        .build();
            case UPDATE_TASK_NAME:
                return UpdateTaskNameLogDTO.buildWithOperationStringID()
                        .id(event.getId())
                        .user(event.getUser())
                        .operationTime(event.getOperationTime())
                        .operationString(event.getOperationString())
                        .operationTypeEnum(event.getOperationTypeEnum())
                        .group(event.getGroup())
                        .build();
            case UPDATE_TASK_TYPE:
                return UpdateTaskTypeLogDTO.buildWithOperationStringID()
                        .id(event.getId())
                        .user(event.getUser())
                        .operationTime(event.getOperationTime())
                        .operationString(event.getOperationString())
                        .operationTypeEnum(event.getOperationTypeEnum())
                        .group(event.getGroup())
                        .build();
            default:
                throw new IllegalArgumentException("Unknown Operation ENUM : " + event.getOperationTypeEnum()+
                " In function createEventDTO");
        }


    }

    public static Event createEventEntity(EventDTO eventDTO){
        return Event.builder()
                .id(eventDTO.getId())
                .operationString(eventDTO.getOperationString())
                .user(eventDTO.getUser())
                .group(eventDTO.getGroup())
                .operationTime(eventDTO.getOperationTime())
                .operationTypeEnum(eventDTO.getOperationTypeEnum())
                .build();
    }
}
