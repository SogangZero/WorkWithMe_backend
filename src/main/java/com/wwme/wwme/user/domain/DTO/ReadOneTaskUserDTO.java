package com.wwme.wwme.user.domain.DTO;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
public class ReadOneTaskUserDTO {
    private Long user_id;
    private String nickname;
    private Long profile_image_id;
    private Boolean is_done;
}
