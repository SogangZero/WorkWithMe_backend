package com.wwme.wwme.notification.service;

import com.wwme.wwme.notification.domain.NotificationSetting;
import com.wwme.wwme.user.domain.User;
import com.wwme.wwme.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


/**
 * Service for managing notification settings.
 * Provides simple CRUD operations
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationSettingService {
    private final UserRepository userRepository;

    /**
     * Updates registration token used by Firebase Cloud Messaging for a specific user
     * @param registrationToken New registration token to be updated
     * @param user The user associated with the registrationToken
     */
    public void updateFcmRegistrationToken(String registrationToken, User user) {
        updateFcmRegistrationToken(registrationToken, user.getId());
    }

    /**
     * Updates registration token used by Firebase Cloud Messaging for a specific user
     * @param registrationToken New registration token to be updated
     * @param userId The userId of user associated with the registrationToken
     */
    @Transactional
    public void updateFcmRegistrationToken(String registrationToken, long userId) {
        log.info("""
                Updating Registration Token
                userId: {}
                registrationToken: {}""",
                userId, registrationToken);

        userRepository.updateRegistrationToken(registrationToken, userId);
    }

    /**
     * Updates the notification settings for a specific user.
     * @param onDueDate Whether to send notifications on due dates.
     * @param onMyTaskCreation Whether to send notifications when a task with the user is created.
     * @param onMyTaskChange Whether to send notifications when a task associated with the user in changed.
     * @param onGroupEntrance Whether to send notifications when a new user enters a existing group.
     * @param user The user for whom the settings are being updated.
     */
    public void updateNotificationSetting(boolean onDueDate, boolean onMyTaskCreation,
                                          boolean onMyTaskChange, boolean onGroupEntrance,
                                          User user) {
        updateNotificationSetting(onDueDate, onMyTaskCreation,
                onMyTaskChange, onGroupEntrance, user.getId());
    }

    /**
     * Updates the notification settings for a specific user.
     * @param onDueDate Whether to send notifications on due dates.
     * @param onMyTaskCreation Whether to send notifications when a task with the user is created.
     * @param onMyTaskChange Whether to send notifications when a task associated with the user in changed.
     * @param onGroupEntrance Whether to send notifications when a new user enters a existing group.
     * @param userId The userId of user for whom the settings are being updated.
     */
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

    public NotificationSetting getNotificationSetting(User user) {
        return user.getNotificationSetting();
    }
}
