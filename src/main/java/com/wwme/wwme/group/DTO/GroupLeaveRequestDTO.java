package com.wwme.wwme.group.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class GroupLeaveRequestDTO {
    @JsonProperty("group_id")
    long groupId;
}
