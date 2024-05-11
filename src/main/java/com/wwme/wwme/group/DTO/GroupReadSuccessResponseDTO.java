package com.wwme.wwme.group.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class GroupReadSuccessResponseDTO {
    boolean success;

    @JsonProperty("group_name")
    String groupName;

    @JsonProperty("group_color")
    String groupColor;

    @JsonProperty("user")
    List<UserDTO> userList = new ArrayList<>();

    @Data
    public static class UserDTO {
        @JsonProperty("user_id")
        long userId;
    }

    public void addUserDTOToList(UserDTO userDTO) {
        userList.add(userDTO);
    }
}
