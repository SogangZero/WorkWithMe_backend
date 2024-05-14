package com.wwme.wwme.task.domain.DTO;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@RequiredArgsConstructor
@Getter
@Setter
public class TaskDTO {
    private Long id;
    @JsonProperty("task_name")
    private String taskName;

    @JsonProperty("start_time")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSSSS")
    private LocalDateTime startTime;

    @JsonProperty("end_time")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSSSS")
    private LocalDateTime endTime;

    @JsonProperty("task_type")
    private String taskType;
    @JsonProperty("is_done")
    private Boolean isDone;
    @JsonProperty("tag_id")
    private Long tagId;
    @JsonProperty("group_id")
    private Long groupId;
    @JsonProperty("user_id")
    private Long userId;
}
