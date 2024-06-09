package com.wwme.wwme.user.domain.DTO;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ReadOneTaskUserDTO {
    private Long user_id;
    private String nickname;
    private Long profile_image_id;
    private Boolean is_done;
}
