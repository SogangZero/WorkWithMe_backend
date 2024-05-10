package com.wwme.wwme.task.domain.DTO;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@RequiredArgsConstructor
@Getter
@Setter
public class TagDTO {
    private Long id;
    private Long group_id;
    private String tag_name;
}
