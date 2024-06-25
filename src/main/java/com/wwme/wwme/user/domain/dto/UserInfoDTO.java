package com.wwme.wwme.user.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class UserInfoDTO {
    private Long user_id;
    private String nickname;
    private Long profile_image_id;
}
