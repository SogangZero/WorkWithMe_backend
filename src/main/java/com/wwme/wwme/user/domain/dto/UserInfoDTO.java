package com.wwme.wwme.user.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserInfoDTO {
    private Boolean success;
    private String nickname;
    private Integer profile_image_id;
}
