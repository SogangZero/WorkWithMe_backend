package com.wwme.wwme.group.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class InvitationAcceptRequestDTO {
    String code;

    @JsonProperty("group_color")
    String groupColor;
}
