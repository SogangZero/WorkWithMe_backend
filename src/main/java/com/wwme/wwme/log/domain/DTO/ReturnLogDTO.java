package com.wwme.wwme.log.domain.DTO;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@Builder
public class ReturnLogDTO {
    private Long log_id;
    private Long task_id;
    private String log_text;
    private LocalDateTime log_time;

    @JsonProperty(namespace = "is_task_log")
    private Boolean is_task_log;
}
