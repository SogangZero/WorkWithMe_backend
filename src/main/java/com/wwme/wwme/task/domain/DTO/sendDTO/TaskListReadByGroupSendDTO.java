package com.wwme.wwme.task.domain.DTO.sendDTO;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.io.Serializable;
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
    public static class Task implements Serializable {
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

        @JsonProperty("tag_name")
        private String tagName;

        @JsonProperty("is_done_total")
        private Boolean isDoneTotal;

        @JsonProperty("is_done_me")
        private Boolean isDoneMe;

        @JsonProperty("is_mine")
        private Boolean isMine;

        @JsonProperty("done_user_count")
        private Integer doneUserCount;

        @JsonProperty("total_user_count")
        private Integer totalUserCount;

        @JsonProperty("doing_nickname")
        private String doingNickname;
    }
}
