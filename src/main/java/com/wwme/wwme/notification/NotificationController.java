package com.wwme.wwme.notification;


import com.google.api.client.json.Json;
import com.google.gson.JsonObject;
import com.wwme.wwme.group.DTO.DataWrapDTO;
import com.wwme.wwme.login.aop.Login;
import com.wwme.wwme.notification.DTO.NotificationSettingUpdateRequestDTO;
import com.wwme.wwme.notification.DTO.SetRegistrationTokenDTO;
import com.wwme.wwme.notification.service.NotificationHistoryService;
import com.wwme.wwme.notification.service.NotificationSettingService;
import com.wwme.wwme.schedule.NotificationSchedule;
import com.wwme.wwme.user.domain.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

@RestController
@RequiredArgsConstructor
@RequestMapping("/notification")
@Slf4j
public class NotificationController {
    private final NotificationService notificationService;
    private final NotificationSettingService notificationSettingService;
    private final NotificationHistoryService notificationHistoryService;

    @PostMapping("/token")
    ResponseEntity<?> setUserFcmRegistrationToken(
            @RequestBody SetRegistrationTokenDTO requestDTO,
            @Login User user
    ) {
        log.info("""
                Controller entrance setUserFcmRegistrationToken.
                request: {}
                user: {}""", requestDTO, user);

        try {
            String registrationToken = requestDTO.getRegistrationToken();
            notificationSettingService.updateFcmRegistrationToken(registrationToken, user);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (RuntimeException e) {
            log.error("Error in updating registration token.\n", e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("setting")
    ResponseEntity<?> updateNotificationSetting(
            @RequestBody NotificationSettingUpdateRequestDTO requestDTO,
            @Login User user
    ) {
        log.info("""
                Controller entrance updateNotificationSetting.\s
                request: {}
                user: {}""", requestDTO, user);
        try {
            notificationSettingService.updateNotificationSetting(
                    requestDTO.onDueDate(),
                    requestDTO.onMyTaskCreation(),
                    requestDTO.onMyTaskChange(),
                    requestDTO.onGroupEntrance(),
                    user
            );
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (RuntimeException e) {
            log.error("Error in updating notification setting", e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping
    ResponseEntity<?> notificationHistoryListReadOfUser(
            @RequestParam(value = "last_id", required = false) Long lastId,
            @Login User user
    ) {
        log.info("""
                Controller entrance notificationHistoryListReadOfUser.
                request: lastId: {}
                user: {}""", lastId, user);
        try {
            var notifications = notificationHistoryService
                    .getNotificationHistoryOfUser(user, lastId);

            var responseDTO = new NotificationHistoryListDTO();
            notifications.forEach(notification -> {
                var notificationDTO = NotificationHistoryListDTO.NotificationHistoryDTO.builder()
                        .title(notification.getNotificationTitle())
                        .body(notification.getNotificationBody())
                        .type(notification.getType().toString())
                        .taskId(notification.getTaskId())
                        .groupId(notification.getGroupId())
                        .build();
                responseDTO.add(notificationDTO);
            });
            return new ResponseEntity<>(responseDTO, HttpStatus.OK);
        } catch (Exception e) {
            log.error("Error in reading notification history", e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
