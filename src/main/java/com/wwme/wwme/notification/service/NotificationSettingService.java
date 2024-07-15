package com.wwme.wwme.notification.service;

import com.wwme.wwme.user.domain.User;
import com.wwme.wwme.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationSettingService {
    private final UserRepository userRepository;

    public void updateFcmRegistrationToken(String registrationToken, User user) {
        updateFcmRegistrationToken(registrationToken, user.getId());
    }

    @Transactional
    public void updateFcmRegistrationToken(String registrationToken, long userId) {
        log.info("""
                Updating Registration Token
                userId: {}
                registrationToken: {}""",
                userId, registrationToken);

        userRepository.updateRegistrationToken(registrationToken, userId);
    }

    public void updateNotificationSetting(boolean onDueDate, boolean onMyTaskCreation,
                                          boolean onMyTaskChange, boolean onGroupEntrance,
                                          User user) {
        updateNotificationSetting(onDueDate, onMyTaskCreation,
                onMyTaskChange, onGroupEntrance, user.getId());
    }

    @Transactional
    public void updateNotificationSetting(boolean onDueDate, boolean onMyTaskCreation,
                                          boolean onMyTaskChange, boolean onGroupEntrance,
                                          long userId) {
        log.info("""
                Updating Notification Setting
                onDueDate: {}
                onMyTaskCreation: {}
                onMyTaskChange: {}
                onGroupEntrance: {}
                userId: {}""",
                onDueDate, onMyTaskCreation, onMyTaskChange, onGroupEntrance, userId);

        userRepository.updateNotificationSetting(onDueDate, onMyTaskCreation, onMyTaskChange, onGroupEntrance, userId);
    }
}
