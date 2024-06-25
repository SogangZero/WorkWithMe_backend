package com.wwme.wwme.task.domain.DTO.sendDTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CUTaskUserDTO {
    private Long user_id;
    private String nickname;
    private Long profile_image_id;
    private Boolean is_done;
}
