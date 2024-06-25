package com.wwme.wwme.task.domain.DTO.sendDTO;

import com.wwme.wwme.user.domain.dto.ReadOneTaskUserDTO;
import com.wwme.wwme.user.domain.User;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import com.wwme.wwme.user.domain.dto.ReadOneTaskUserDTO;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReadOneTaskSendDTO {
    private Long task_id;
    private String task_name;
    private Integer is_done_count;
    private List<ReadOneTaskUserDTO> user_list;
    private String task_type;
    private LocalDateTime start_time;
    private LocalDateTime end_time;

    //added 0620
    private Boolean is_done_personal;
    private Boolean is_done_total;
    private ROT_groupDTO group;
    private ROT_tagDTO tag;


    //Without is_done_count, total_user_count, user_list, is_done_personal, is_done_total
    //and group, tag dtos.

    @Builder
    public ReadOneTaskSendDTO(Long task_id, String task_name,
                              String task_type, LocalDateTime start_time,
                              LocalDateTime end_time, ROT_groupDTO group, ROT_tagDTO tag) {
        this.task_id = task_id;
        this.task_name = task_name;
        this.task_type = task_type;
        this.start_time = start_time;
        this.end_time = end_time;
        this.group = group;
        this.tag = tag;
    }
}
