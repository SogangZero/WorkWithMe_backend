package com.wwme.wwme.task.domain.DTO.sendDTO;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class CUTaskSendDTO {
    private Long task_id;
    private String task_name;
    private CUTaskGroupDTO group;
    private CUTaskTagDTO tag;
    private Integer is_done_count;
    private Boolean is_done_personal;
    private Boolean is_done_total;
    private List<CUTaskUserDTO> user_list;
    private String task_type;
    private LocalDateTime start_time;
    private LocalDateTime end_time;
}
