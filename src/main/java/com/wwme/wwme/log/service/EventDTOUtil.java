package com.wwme.wwme.log.service;

import com.wwme.wwme.log.domain.DTO.*;
import com.wwme.wwme.log.domain.Event;

public class EventDTOUtil {

    public static <T extends EventDTO> T convertToSpecificDTO(EventDTO event, Class<T> dtoClass) {
        EventDTO eventDTO = (event);

        if (dtoClass.isInstance(eventDTO)) {
            return dtoClass.cast(eventDTO);
        } else {
            throw new IllegalArgumentException("EventDTO is not of type " + dtoClass.getSimpleName());
        }
    }

    private EventDTO convertToDTO(Event event){
        EventDTO.EventDTOBuilder builder = switch (event.getOperationTypeEnum()) {
            case CREATE_TASK -> CreateTaskLogDTO.builder();
            case DELETE_TASK -> DeleteTaskLogDTO.builder();
            case UPDATE_TASK_CHANGE_TAG -> UpdateTaskChangeTagDTO.builder();
            case UPDATE_TASK_NAME -> UpdateTaskNameLogDTO.builder();
            case UPDATE_TASK_DELETE_TAG -> UpdateTaskDeleteTagDTO.builder();
            case UPDATE_TASK_DUE_DATE -> UpdateTaskDueDateLogDTO.builder();
            case UPDATE_TASK_TYPE -> UpdateTaskTypeLogDTO.builder();
            default ->
                    throw new IllegalArgumentException("operationTypeEnum Error : unsupported enum : " + event.getOperationTypeEnum());
        };

        return builder
                .id(event.getId())
                .operationTime(event.getOperationTime())
                .operationTypeEnum(event.getOperationTypeEnum())
                .group(event.getGroup())
                .user(event.getUser())
                .operationString(event.getOperationString())
                .build();
    }


}
