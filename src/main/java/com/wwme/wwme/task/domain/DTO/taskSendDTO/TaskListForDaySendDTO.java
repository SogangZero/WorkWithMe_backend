package com.wwme.wwme.task.domain.DTO.taskSendDTO;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TaskListForDaySendDTO {
    private Boolean isComplete;
    private Long taskId;
    private String taskName;
    private Long tagId;
    private String tagName;
    private LocalDate endDate;
}
