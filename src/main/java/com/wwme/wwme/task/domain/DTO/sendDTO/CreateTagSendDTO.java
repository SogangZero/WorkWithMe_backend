package com.wwme.wwme.task.domain.DTO.sendDTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class CreateTagSendDTO {
    private Long tag_id;
    private String tag_name;
}
