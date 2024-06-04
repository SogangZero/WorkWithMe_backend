package com.wwme.wwme.group.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class GroupUserListReadResponseSuccessDTO {
    @JsonProperty("user_list")
    List<User> userList;
    @JsonProperty("group_code")
    String groupCode;

    @Data
    public static class User {
        @JsonProperty("nickname")
        String nickname;

        @JsonProperty("profile_image_id")
        int profileImageId;
    }
}
