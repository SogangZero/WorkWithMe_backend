package com.wwme.wwme.log.domain.DTO;

import java.time.LocalDateTime;

public class UpdateTaskDueDateLogDTO extends EventDTO{

    LocalDateTime previousDueDate;
    LocalDateTime updatedDueDate;

    @Override
    public String getOperationStr() {
        return previousDueDate.toString() + "|" + updatedDueDate.toString();

    }
}
