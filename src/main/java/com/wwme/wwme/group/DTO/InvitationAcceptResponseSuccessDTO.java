package com.wwme.wwme.group.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class InvitationAcceptResponseSuccessDTO {
    boolean success;

    @JsonProperty("group_id")
    long groupId;
}
