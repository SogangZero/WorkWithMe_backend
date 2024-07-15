package com.wwme.wwme.log.service;

import com.wwme.wwme.log.domain.DTO.*;
import com.wwme.wwme.log.domain.Event;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class EventDTOFactory {
    public static EventDTO createEventDTO(Event event){
        switch (event.getOperationTypeEnum()){
            case CREATE_TASK:
                CreateTaskLogDTO createTaskLogDTO = CreateTaskLogDTO.buildWithOperationStringID()
                        .id(event.getId())
                        .user(event.getUser())
                        .task(event.getTask())
                        .operationTime(event.getOperationTime())
                        .operationString(event.getOperationString())
                        .operationTypeEnum(event.getOperationTypeEnum())
                        .group(event.getGroup())
                        .build();
                log.info(createTaskLogDTO.toString());
                createTaskLogDTO.setSpecificFields();
                return createTaskLogDTO;
            case DELETE_TASK:
                DeleteTaskLogDTO deleteTaskLogDTO = DeleteTaskLogDTO.buildWithOperationStringID()
                        .id(event.getId())
                        .user(event.getUser())
                        .task(event.getTask())
                        .operationTime(event.getOperationTime())
                        .operationString(event.getOperationString())
                        .operationTypeEnum(event.getOperationTypeEnum())
                        .group(event.getGroup())
                        .build();
                deleteTaskLogDTO.setSpecificFields();
                return deleteTaskLogDTO;
            case UPDATE_TASK_CHANGE_TAG:
                UpdateTaskChangeTagDTO updateTaskChangeTagDTO = UpdateTaskChangeTagDTO.buildWithOperationStringID()
                        .id(event.getId())
                        .user(event.getUser())
                        .task(event.getTask())
                        .operationTime(event.getOperationTime())
                        .operationString(event.getOperationString())
                        .operationTypeEnum(event.getOperationTypeEnum())
                        .group(event.getGroup())
                        .build();
                updateTaskChangeTagDTO.setSpecificFields();

                return updateTaskChangeTagDTO;
            case UPDATE_TASK_DELETE_TAG:
                UpdateTaskDeleteTagDTO updateTaskDeleteTagDTO = UpdateTaskDeleteTagDTO.buildWithOperationStringID()
                        .id(event.getId())
                        .user(event.getUser())
                        .task(event.getTask())
                        .operationTime(event.getOperationTime())
                        .operationString(event.getOperationString())
                        .operationTypeEnum(event.getOperationTypeEnum())
                        .group(event.getGroup())
                        .build();
                updateTaskDeleteTagDTO.setSpecificFields();

                return updateTaskDeleteTagDTO;
            case UPDATE_TASK_DUE_DATE:
                UpdateTaskDueDateLogDTO updateTaskDueDateLogDTO = UpdateTaskDueDateLogDTO.buildWithOperationStringID()
                        .id(event.getId())
                        .user(event.getUser())
                        .task(event.getTask())
                        .operationTime(event.getOperationTime())
                        .operationString(event.getOperationString())
                        .operationTypeEnum(event.getOperationTypeEnum())
                        .group(event.getGroup())
                        .build();
                updateTaskDueDateLogDTO.setSpecificFields();

                return updateTaskDueDateLogDTO;
            case UPDATE_TASK_NAME:
                UpdateTaskNameLogDTO updateTaskNameLogDTO = UpdateTaskNameLogDTO.buildWithOperationStringID()
                        .id(event.getId())
                        .user(event.getUser())
                        .task(event.getTask())
                        .operationTime(event.getOperationTime())
                        .operationString(event.getOperationString())
                        .operationTypeEnum(event.getOperationTypeEnum())
                        .group(event.getGroup())
                        .build();
                updateTaskNameLogDTO.setSpecificFields();

                return updateTaskNameLogDTO;
            case UPDATE_TASK_TYPE:
                UpdateTaskTypeLogDTO updateTaskTypeLogDTO = UpdateTaskTypeLogDTO.buildWithOperationStringID()
                        .id(event.getId())
                        .user(event.getUser())
                        .task(event.getTask())
                        .operationTime(event.getOperationTime())
                        .operationString(event.getOperationString())
                        .operationTypeEnum(event.getOperationTypeEnum())
                        .group(event.getGroup())
                        .build();
                updateTaskTypeLogDTO.setSpecificFields();

                return updateTaskTypeLogDTO;
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
                .task(eventDTO.getTask())
                .operationTime(eventDTO.getOperationTime())
                .operationTypeEnum(eventDTO.getOperationTypeEnum())
                .build();
    }
}
