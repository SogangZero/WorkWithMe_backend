package com.wwme.wwme.task.domain.DTO.sendDTO;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class TaskListReadByGroupSendDTO {
    @JsonProperty("data")
    List<Task> taskList;

    @Data
    @AllArgsConstructor
    public static class Task {
        @JsonProperty("task_id")
        private Long taskId;

        @JsonProperty("task_name")
        private String taskName;

        @JsonProperty("end_time")
        private LocalDateTime endTime;

        @JsonProperty("task_type")
        private String taskType;

        @JsonProperty("tag_id")
        private Long tagId;

        @JsonProperty("is_done_total")
        private Boolean isDoneTotal;

        @JsonProperty("is_done_me")
        private Boolean isDoneMe;

        @JsonProperty("done_user_count")
        private Integer doneUserCount;

        @JsonProperty("doing_nickname")
        private String doingNickname;
    }
}
