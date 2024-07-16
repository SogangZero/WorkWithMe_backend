package com.wwme.wwme.notification;

import com.wwme.wwme.notification.domain.NotificationHistory;
import com.wwme.wwme.user.domain.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationHistoryRepository extends JpaRepository<NotificationHistory, Long> {
    @Query("SELECT nh FROM NotificationHistory nh " +
            "WHERE nh.user=:user " +
            "AND (:lastId IS NULL OR nh.id > :lastId) " +
            "ORDER BY nh.notificationTime, nh.id ASC ")
    List<NotificationHistory> findAllByUserAndLastId(
           User user, Long lastId,
           Pageable pageable);
}
