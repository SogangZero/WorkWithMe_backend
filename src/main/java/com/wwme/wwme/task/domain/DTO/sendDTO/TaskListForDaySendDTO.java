package com.wwme.wwme.task.domain.DTO.sendDTO;

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
    private LocalDate endDate;
    private ROT_tagDTO tag;
}
