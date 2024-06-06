package com.wwme.wwme.task.domain.DTO;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@RequiredArgsConstructor
@Getter
@Setter
public class TagDTO {
    private Long id;

    @JsonProperty("group_id")
    private Long groupId;

    @JsonProperty("tag_name")
    private String tagName;
}
