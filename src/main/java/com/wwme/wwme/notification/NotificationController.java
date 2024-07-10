package com.wwme.wwme.notification;


import com.google.api.client.json.Json;
import com.google.gson.JsonObject;
import com.wwme.wwme.login.aop.Login;
import com.wwme.wwme.notification.DTO.NotificationSettingUpdateRequestDTO;
import com.wwme.wwme.notification.DTO.SetRegistrationTokenDTO;
import com.wwme.wwme.schedule.NotificationSchedule;
import com.wwme.wwme.user.domain.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/notification")
@Slf4j
public class NotificationController {
    private final NotificationService notificationService;

    @PostMapping("/token")
    ResponseEntity<?> setUserFcmRegistrationToken(
            @RequestBody SetRegistrationTokenDTO requestDTO,
            @Login User user
            ) {
        try {
            String registrationToken = requestDTO.getRegistrationToken();
            log.info("Update registration token: {}", registrationToken);

            notificationService.updateRegistrationToken(registrationToken, user);

            return new ResponseEntity<>(HttpStatus.OK);
        }
        catch (Exception e) {
            log.error("Error in updating registration token ", e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("setting")
    ResponseEntity<?> updateNotificationSetting(
            @RequestBody NotificationSettingUpdateRequestDTO requestDTO,
            @Login User user
    ) {
        try {
            log.info("Update notification setting: {}", requestDTO.toString());
            notificationService.updateNotificationSetting(
                    requestDTO.onDueDate(),
                    requestDTO.onMyTaskCreation(),
                    requestDTO.onMyTaskChange(),
                    requestDTO.onGroupEntrance(),
                    user
            );
            return new ResponseEntity<>(HttpStatus.OK);
        }
        catch (Exception e) {
            log.error("Error in updating notification setting", e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    private final NotificationSchedule notificationSchedule;
    @GetMapping("test")
    void test() {
        //notificationSchedule.sendOnDueDateNotifications();
        //notificationService.sendTest();
    }
}
