package com.wwme.wwme.notification.service;

import com.wwme.wwme.notification.DTO.NotificationHistoryListResponseDTO;
import com.wwme.wwme.notification.NotificationHistory;

import java.util.List;

public class NotificationDtoConverter {
    public NotificationHistoryListResponseDTO convert(List<NotificationHistory> notifications) {
        var responseDTO = new NotificationHistoryListResponseDTO();
        notifications.forEach(notification -> {
            var notificationDTO = NotificationHistoryListResponseDTO.NotificationHistoryDTO.builder()
                    .title(notification.getNotificationTitle())
                    .body(notification.getNotificationBody())
                    .type(notification.getType().toString())
                    .taskId(notification.getTaskId())
                    .groupId(notification.getGroupId())
                    .build();
            responseDTO.add(notificationDTO);
        });
        return responseDTO;
    }
}
