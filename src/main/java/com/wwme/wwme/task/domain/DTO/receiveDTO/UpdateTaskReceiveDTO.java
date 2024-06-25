package com.wwme.wwme.task.domain.DTO.receiveDTO;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateTaskReceiveDTO {
    private Long task_id;
    private String task_name;
    private LocalDateTime end_time;
    private String task_type;
    private Long tag_id;
    private Long todo_user_id;
}
