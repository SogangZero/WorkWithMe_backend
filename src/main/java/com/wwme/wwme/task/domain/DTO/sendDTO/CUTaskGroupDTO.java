package com.wwme.wwme.task.domain.DTO.sendDTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CUTaskGroupDTO {
    private Long group_id;
    private String group_name;
    private String group_color;
    private Integer num_people;
}
