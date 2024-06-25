package com.wwme.wwme.group.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class InvitationAcceptRequestDTO {
    @JsonProperty("group_code")
    String groupCode;

    @JsonProperty("group_color")
    Long groupColor;
}
