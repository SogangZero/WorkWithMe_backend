package com.wwme.wwme.log.service;

import com.wwme.wwme.log.domain.DTO.*;
import com.wwme.wwme.log.domain.DTO.tag.CreateTagLogDTO;
import com.wwme.wwme.log.domain.DTO.tag.DeleteTagLogDTO;
import com.wwme.wwme.log.domain.DTO.tag.UpdateTagNameLogDTO;
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
                        .currentTaskName(event.getCurrentTaskName())
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
                        .currentTaskName(event.getCurrentTaskName())
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
                        .currentTaskName(event.getCurrentTaskName())
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
                        .currentTaskName(event.getCurrentTaskName())
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
                        .currentTaskName(event.getCurrentTaskName())
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
                        .currentTaskName(event.getCurrentTaskName())
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
                        .currentTaskName(event.getCurrentTaskName())
                        .build();
                updateTaskTypeLogDTO.setSpecificFields();

                return updateTaskTypeLogDTO;
            case CREATE_TAG:
                CreateTagLogDTO createTagLogDTO = CreateTagLogDTO.buildWithOperationStringID()
                        .id(event.getId())
                        .user(event.getUser())
                        .task(event.getTask())
                        .operationTime(event.getOperationTime())
                        .operationString(event.getOperationString())
                        .operationTypeEnum(event.getOperationTypeEnum())
                        .group(event.getGroup())
                        .currentTaskName(event.getCurrentTaskName())
                        .build();
                return createTagLogDTO;
            case DELETE_TAG:
                DeleteTagLogDTO deleteTagLogDTO = DeleteTagLogDTO.buildWithOperationStringID()
                        .id(event.getId())
                        .user(event.getUser())
                        .task(event.getTask())
                        .operationTime(event.getOperationTime())
                        .operationString(event.getOperationString())
                        .operationTypeEnum(event.getOperationTypeEnum())
                        .group(event.getGroup())
                        .currentTaskName(event.getCurrentTaskName())
                        .build();
                return deleteTagLogDTO;
            case UPDATE_TAG_NAME:
                UpdateTagNameLogDTO updateTagNameLogDTO = UpdateTagNameLogDTO.buildWithOperationStringID()
                        .id(event.getId())
                        .user(event.getUser())
                        .task(event.getTask())
                        .operationTime(event.getOperationTime())
                        .operationString(event.getOperationString())
                        .operationTypeEnum(event.getOperationTypeEnum())
                        .group(event.getGroup())
                        .currentTaskName(event.getCurrentTaskName())
                        .build();
                return updateTagNameLogDTO;
            default:
                throw new IllegalArgumentException("Unknown Operation ENUM : " + event.getOperationTypeEnum()+
                " In function createEventDTO");
        }


    }

}
