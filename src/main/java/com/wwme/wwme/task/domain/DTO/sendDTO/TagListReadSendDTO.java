package com.wwme.wwme.task.domain.DTO.sendDTO;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
public class TagListReadSendDTO {
    private Long tag_id;
    private String tag_name;
}
