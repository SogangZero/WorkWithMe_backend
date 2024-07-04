package com.wwme.wwme.log.domain.DTO;

import lombok.*;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateTaskLogDTO extends EventDTO{
    public String newTaskName;

    @Override
    public String getOperationStr() {
        return newTaskName;
    }
}
