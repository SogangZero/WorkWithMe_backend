package com.wwme.wwme.task.domain.DTO.taskReceiveDTO;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@RequiredArgsConstructor
public class UpdateTaskReceiveDTO {
    private Long task_id;
    private String task_name;
    private LocalDateTime start_time;
    private LocalDateTime end_time;
    private String task_type;
    private Long tag_id;
    private Long group_id;
    private Boolean is_done_total;
    private Boolean is_done_personal;
}
