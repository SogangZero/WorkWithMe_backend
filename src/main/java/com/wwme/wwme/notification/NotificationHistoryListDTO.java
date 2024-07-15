package com.wwme.wwme.notification;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class NotificationHistoryListDTO {
    @JsonProperty("data")
    List<NotificationHistoryDTO> data = new ArrayList<>();

    public void add(NotificationHistoryDTO dto) {
        data.add(dto);
    }

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
