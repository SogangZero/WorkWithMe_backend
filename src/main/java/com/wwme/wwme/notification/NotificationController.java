package com.wwme.wwme.notification;


import com.wwme.wwme.login.aop.Login;
import com.wwme.wwme.notification.DTO.NotificationSettingUpdateRequestDTO;
import com.wwme.wwme.notification.DTO.SetRegistrationTokenRequestDTO;
import com.wwme.wwme.notification.domain.NotificationSetting;
import com.wwme.wwme.notification.service.NotificationDtoConverter;
import com.wwme.wwme.notification.service.NotificationHistoryService;
import com.wwme.wwme.notification.service.NotificationSettingService;
import com.wwme.wwme.user.domain.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/notification")
@Slf4j
public class NotificationController {
    private final NotificationSettingService notificationSettingService;
    private final NotificationHistoryService notificationHistoryService;
    private final NotificationDtoConverter dtoConverter;

    @PostMapping("/token")
    ResponseEntity<?> setUserFcmRegistrationToken(
            @RequestBody SetRegistrationTokenRequestDTO requestDTO,
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
            log.error("Runtime Error in updating registration token.\n", e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/setting")
    ResponseEntity<?> readNotificationSetting(
            @Login User user
    ) {
       log.info("""
               Controller entrance readNotificationSetting""");

       try {
           NotificationSetting notificationSetting = notificationSettingService.getNotificationSetting(user);
           return new ResponseEntity<>(notificationSetting, HttpStatus.OK);
       }
       catch (RuntimeException e) {
           log.error("Runtime Error in reading notification setting\n", e);
           return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
       }
    }

    @PutMapping("/setting")
    ResponseEntity<?> updateNotificationSetting(
            @RequestBody NotificationSettingUpdateRequestDTO requestDTO,
            @Login User user
    ) {
        log.info("""
                Controller entrance updateNotificationSetting.
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
            log.error("Runtime Error in updating notification setting\n", e);
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
            var responseDTO = dtoConverter.convert(notifications);
            return new ResponseEntity<>(responseDTO, HttpStatus.OK);
        } catch (RuntimeException e) {
            log.error("Runtime Error in reading notification history\n", e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
