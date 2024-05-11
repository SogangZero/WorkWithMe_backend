package com.wwme.wwme.group.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
public class GroupUpdateSuccessDTO {
    boolean success;

    @JsonProperty("group_id")
    long groupId;
}
