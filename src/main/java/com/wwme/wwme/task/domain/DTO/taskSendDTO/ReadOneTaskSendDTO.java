package com.wwme.wwme.task.domain.DTO.taskSendDTO;

import com.wwme.wwme.user.domain.DTO.ReadOneTaskUserDTO;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
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
