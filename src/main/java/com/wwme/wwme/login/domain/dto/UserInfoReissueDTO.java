package com.wwme.wwme.login.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class UserInfoReissueDTO {
    private Long user_id;
    private String nickname;
    private Long profile_image_id;
}
