package com.wwme.wwme.notification.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Builder
public class NotificationHistoryListResponseDTO {
    @JsonProperty("data")
    List<NotificationHistoryDTO> data;

    @Builder
    public static class NotificationHistoryDTO {
        @JsonProperty("title")
        private String title;

        @JsonProperty("body")
        private String body;

        @JsonProperty("type")
        private String type;

        @JsonProperty("task_id")
        private Long taskId;

        @JsonProperty("group_id")
        private Long groupId;
    }
}
