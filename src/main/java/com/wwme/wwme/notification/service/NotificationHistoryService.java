package com.wwme.wwme.notification.service;

import com.wwme.wwme.notification.NotificationHistory;

import com.wwme.wwme.notification.NotificationHistoryRepository;
import com.wwme.wwme.user.domain.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

 /**
  * Service for managing notification history.
  * Provides simple CRUD operations
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationHistoryService {
    private final NotificationHistoryRepository notificationHistoryRepository;

     /**
      * Gets last 20 notification history of user.
      * @param user The user of notification history to be retrieved.
      * @param lastId The last id of notification history previously gotten.
      *               The next 20 notification histories are retrieved after lastId.
      * @return List of NotificationHistory entity.
      */
    public List<NotificationHistory> getNotificationHistoryOfUser(User user, Long lastId) {
        log.info("""
                Getting Notification History of User
                user: {}
                lastId: {}""", user, lastId);

        Pageable pageRequest = PageRequest.of(0, 20);
        return notificationHistoryRepository.findAllByUserAndLastId(user, lastId, pageRequest);
    }
}
