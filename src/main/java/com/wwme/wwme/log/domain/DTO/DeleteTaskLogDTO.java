package com.wwme.wwme.log.domain.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeleteTaskLogDTO extends EventDTO{

    private String deletedTaskName;

    @Override
    public String getOperationStr() {
        return deletedTaskName;
    }
}
