package com.wwme.wwme.log.domain.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateTaskNameLogDTO extends EventDTO{

    private String beforeTaskName;
    private String afterTaskName;
    @Override
    public String getOperationStr() {
        return beforeTaskName+"|"+afterTaskName;
    }
}
