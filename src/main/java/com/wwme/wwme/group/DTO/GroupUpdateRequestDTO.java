package com.wwme.wwme.group.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class GroupUpdateRequestDTO {
    @JsonProperty("group_id")
    long groupId;
    @JsonProperty("group_name")
    String groupName;
    @JsonProperty("group_color")
    Long groupColor;
}
