package com.wwme.wwme.group.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class GroupUpdateSuccessResponseDTO {
    boolean success;

    @JsonProperty("group_id")
    long groupId;
}
