package com.wwme.wwme.user.domain.DTO;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
public class ReadOneTaskUserDTO {
    private Long userId;
    private String nickname;
    private Long profileImageId;
    private Boolean isDone;
}
