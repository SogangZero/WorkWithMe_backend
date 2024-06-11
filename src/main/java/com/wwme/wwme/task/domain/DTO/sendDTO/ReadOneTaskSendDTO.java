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
    private String tag_name;
    private String task_name;
    private Integer is_done_count;
    private Integer total_user_count;
    private List<ReadOneTaskUserDTO> user_list;
    private String task_type;
    private String group_name;
    private LocalDateTime start_time;
    private LocalDateTime end_time;


    //Without is_done_count, total_user_count, and user_list;
    @Builder
    public ReadOneTaskSendDTO(Long task_id,
                              String tag_name,
                              String task_name,
                              String task_type, String group_name, LocalDateTime start_time, LocalDateTime end_time) {
        this.task_id = task_id;
        this.tag_name = tag_name;
        this.task_name = task_name;
        this.task_type = task_type;
        this.group_name = group_name;
        this.start_time = start_time;
        this.end_time = end_time;
    }
}
