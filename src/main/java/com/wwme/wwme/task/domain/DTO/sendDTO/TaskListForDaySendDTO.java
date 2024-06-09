package com.wwme.wwme.task.domain.DTO.sendDTO;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@RequiredArgsConstructor
public class TaskListForDaySendDTO {
    private Boolean isComplete;
    private Long taskId;
    private String taskName;
    private Long tagId;
    private String tagName;
    private LocalDate endDate;
}
