package com.wwme.wwme.task.domain.DTO.receiveDTO;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@RequiredArgsConstructor
public class TaskListReadByGroupReceiveDTO {
    private Long group_id;
    private Boolean is_my_task;
    private String complete_status;
    private LocalDate start_date;
    private LocalDate end_date;
    private Boolean with_due_date;
    private List<Long> tag_list;
}
