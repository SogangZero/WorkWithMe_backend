package com.wwme.wwme.group.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GroupCreateSuccessResponseDTO {
    @JsonProperty("group_id")
    long groupId;

    @JsonProperty("group_code")
    String groupCode;

    @JsonProperty("group_name")
    String groupName;

    @JsonProperty("group_color")
    Long groupColor;
}
