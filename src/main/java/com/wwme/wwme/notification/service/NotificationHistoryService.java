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

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationHistoryService {
    private final NotificationHistoryRepository notificationHistoryRepository;
    public List<NotificationHistory> getNotificationHistoryOfUser(User user, Long lastId) {
        log.info("""
                Getting Notification History of User
                user: {}
                lastId: {}""", user, lastId);

        Pageable pageRequest = PageRequest.of(0, 20);
        return notificationHistoryRepository.findAllByUserAndLastId(user, lastId, pageRequest);
    }
}
