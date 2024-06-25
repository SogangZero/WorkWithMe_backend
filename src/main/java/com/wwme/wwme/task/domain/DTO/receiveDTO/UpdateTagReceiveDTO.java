package com.wwme.wwme.task.domain.DTO.receiveDTO;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
public class UpdateTagReceiveDTO {
    private Long tag_id;
    private String tag_name;
}
