package com.wwme.wwme.task.domain.DTO.sendDTO;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReadTaskListByUserSendDTO {
    private Long task_id;
    private String task_name;
    private LocalDateTime start_time;
    private LocalDateTime end_time;
    private String task_type;
    private Boolean is_done_personal;
    private Boolean is_done_total;

    //0620수정 : tag encapsulation
    private ROT_tagDTO tag;
    private RTL_groupDTO group;
}
