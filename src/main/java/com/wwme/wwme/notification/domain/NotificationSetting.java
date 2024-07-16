package com.wwme.wwme.notification.domain;

import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Embeddable
public class NotificationSetting {
    private String registrationToken;

    private Boolean onDueDate = true;

    private Boolean onMyTaskCreation = true;

    private Boolean onMyTaskChange = true;

    private Boolean onGroupEntrance = true;
}
