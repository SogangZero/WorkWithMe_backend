package com.wwme.wwme.task.domain.DTO.sendDTO;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ROT_groupDTO {
    private Long group_id;
    private String group_name;
    private Long group_color;
    private Integer num_people;
}
