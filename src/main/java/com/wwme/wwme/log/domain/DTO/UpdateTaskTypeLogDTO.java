package com.wwme.wwme.log.domain.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateTaskTypeLogDTO extends EventDTO{

    private String beforeTaskType;
    private String afterTaskType;
    @Override
    public String getOperationStr() {
        return beforeTaskType + "|" + afterTaskType;
    }
}
