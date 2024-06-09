package com.wwme.wwme.group.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
public class GroupReadSuccessResponseDTO {
    @JsonProperty("group_name")
    String groupName;

    @JsonProperty("group_color")
    String groupColor;

    @JsonProperty("user")
    List<UserDTO> userList;

    @Data
    @AllArgsConstructor
    public static class UserDTO {
        @JsonProperty("user_id")
        long userId;
    }
}
