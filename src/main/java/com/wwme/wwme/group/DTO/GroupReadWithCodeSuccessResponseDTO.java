package com.wwme.wwme.group.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class GroupReadWithCodeSuccessResponseDTO {
    @JsonProperty("group_name")
    String groupName;
    @JsonProperty("user")
    List<User> user;

    @Data
    public static class User {
        @JsonProperty("user_id")
        long userId;
        @JsonProperty("nickname")
        String nickname;
    }
}
