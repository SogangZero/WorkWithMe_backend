package com.wwme.wwme.notification.domain;

import com.wwme.wwme.user.domain.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Builder
@AllArgsConstructor
@Getter
@Entity
public class NotificationHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    User user;

    String notificationTitle;
    String notificationBody;
    LocalDateTime notificationTime;

    @Enumerated(EnumType.STRING)
    NotificationType type;
    Long taskId;
    Long groupId;

    public NotificationHistory() {}
}
