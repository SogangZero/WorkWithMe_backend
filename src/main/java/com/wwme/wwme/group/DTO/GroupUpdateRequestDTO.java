package com.wwme.wwme.group.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class GroupUpdateRequestDTO {
    long groupId;
    String groupName;
    String groupColor;
}
