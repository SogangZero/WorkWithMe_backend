package com.wwme.wwme.task.domain.DTO.sendDTO;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@RequiredArgsConstructor
public class GetTaskCountListforMonthSendDTO {
    private List<Integer> number_list;
}
