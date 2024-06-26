package com.wwme.wwme.user.domain.dto;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReadOneTaskUserDTO {
    private Long user_id;
    private String nickname;
    private Long profile_image_id;
    private Boolean is_done;
}