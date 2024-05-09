package com.wwme.wwme.group.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class GroupCreateRequestDTO {
    @JsonProperty("group_name")
    private String groupName;

    @JsonProperty("group_color")
    private String groupColor;
}
