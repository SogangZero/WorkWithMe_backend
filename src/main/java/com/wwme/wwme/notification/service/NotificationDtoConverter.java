package com.wwme.wwme.notification.service;

import com.wwme.wwme.notification.DTO.NotificationHistoryListResponseDTO;
import com.wwme.wwme.notification.domain.NotificationHistory;

import java.util.ArrayList;
import java.util.List;

/**
 * Converter for converting domain objects to DTOs.
 * Used in notification package.
 */
public class NotificationDtoConverter {
    public NotificationHistoryListResponseDTO convert(List<NotificationHistory> notifications) {
        List<NotificationHistoryListResponseDTO.NotificationHistoryDTO> notificationDTOList = new ArrayList<>();
        notifications.forEach(notification -> {
            // map each notification to dto
            var notificationDTO = NotificationHistoryListResponseDTO.NotificationHistoryDTO.builder()
                    .title(notification.getNotificationTitle())
                    .body(notification.getNotificationBody())
                    .type(notification.getType().toString())
                    .taskId(notification.getTaskId())
                    .groupId(notification.getGroupId())
                    .build();
            notificationDTOList.add(notificationDTO);
        });
        return NotificationHistoryListResponseDTO.builder()
                .data(notificationDTOList)
                .build();
    }
}
