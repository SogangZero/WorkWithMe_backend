package com.wwme.wwme.task.domain.DTO.sendDTO;

import com.wwme.wwme.user.domain.DTO.ReadOneTaskUserDTO;
import com.wwme.wwme.user.domain.User;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@RequiredArgsConstructor

public class ReadOneTaskSendDTO {
    private Long task_id;
    private String tag_name;
    private String task_name;
    private Integer is_done_count;
    private Integer total_user_count;
    private List<ReadOneTaskUserDTO> user_list;
    private String task_type;
    private String group_name;
    private LocalDate start_time;
    private LocalDate end_time;
}
