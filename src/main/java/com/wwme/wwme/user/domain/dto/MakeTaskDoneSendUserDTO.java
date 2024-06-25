package com.wwme.wwme.user.domain.dto;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MakeTaskDoneSendUserDTO {
    private Long user_id;
    private String nickname;
    private Long profile_image_id;
    private Boolean is_done;
}
