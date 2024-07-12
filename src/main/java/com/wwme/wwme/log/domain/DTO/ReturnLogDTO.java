package com.wwme.wwme.log.domain.DTO;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class ReturnLogDTO {
    private Long log_id;
    private Long task_id;
    private String log_text;
}
