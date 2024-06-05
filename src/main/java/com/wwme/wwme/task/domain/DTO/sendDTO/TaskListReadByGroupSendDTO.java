package com.wwme.wwme.task.domain.DTO.sendDTO;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@RequiredArgsConstructor
public class TaskListReadByGroupSendDTO {
    private Long task_id;
    private String task_name;
    private LocalDate end_time;
    private String task_type;
    private Long tag_id;
    private Boolean is_done_total;
    private Boolean is_done_me;
    private Integer done_user_count;
    private String doing_nickname;
}
