package com.wwme.wwme.task.domain.DTO.sendDTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CUTaskTagDTO {
    private Long tag_id;
    private String tag_name;
}
