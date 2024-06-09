package com.wwme.wwme.task.domain.DTO.taskSendDTO;

import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GetTaskCountListforMonthSendDTO {
    private List<Integer> number_list;
}
