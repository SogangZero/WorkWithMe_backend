package com.wwme.wwme.log.domain.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class ReceiveLogDTO {
    private Long last_id;
    private Long group_id;
}
