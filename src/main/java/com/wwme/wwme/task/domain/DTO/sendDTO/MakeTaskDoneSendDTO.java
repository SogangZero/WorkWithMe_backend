package com.wwme.wwme.task.domain.DTO.sendDTO;

import com.wwme.wwme.user.domain.dto.MakeTaskDoneSendUserDTO;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MakeTaskDoneSendDTO {
    private Long task_id;
    private String task_name;
    private Long is_done_count;
    private Long total_user_count;
    private List<MakeTaskDoneSendUserDTO> user_list;
    private String task_type;
    private String group_name;
    private LocalDateTime start_time;
    private LocalDateTime end_time;

}
