package com.wwme.wwme.task.domain.DTO.taskReceiveDTO;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@RequiredArgsConstructor
public class CreateTaskReceiveDTO {
    private String task_name;
    private LocalDateTime start_time;
    private LocalDateTime end_time;
    private String task_type;
    private Long tag_id;
    private Long group_id;
    private Long todo_user_id;
}