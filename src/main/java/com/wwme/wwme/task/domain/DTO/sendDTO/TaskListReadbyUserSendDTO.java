package com.wwme.wwme.task.domain.DTO.sendDTO;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@RequiredArgsConstructor
public class TaskListReadbyUserSendDTO {
    private Long task_id;
    private String task_name;
    private LocalDateTime start_time;
    private LocalDateTime end_time;
    private String task_type;
    private Long tag_id;
    private String tag_name;
    private Long group_id;
    private String group_color;
    private Boolean is_done_personal;
    private Boolean is_done_total;
}
