package com.wwme.wwme.user.repository;

import com.wwme.wwme.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User,Long> {
    Optional<User> findByUserKey(String userKey);

    Boolean existsByUserKey(String userKey);

    @Query("SELECT u FROM User u " +
            "JOIN u.userGroup ug " +
            "WHERE ug.group.id = :groupId")
    List<User> findAllByGroupID(Long groupId);

    @Transactional
    @Modifying
    @Query("UPDATE User u " +
            "SET u.notificationSetting.registrationToken=:registrationToken " +
            "WHERE u.id=:userId")
    void updateRegistrationToken(String registrationToken, Long userId);

    @Transactional
    @Modifying
    @Query("UPDATE User u " +
            "SET u.notificationSetting.onDueDate=:onDueDate, " +
            "u.notificationSetting.onMyTaskCreation=:onMyTaskCreation, " +
            "u.notificationSetting.onMyTaskChange=:onMyTaskChange, " +
            "u.notificationSetting.onGroupEntrance=:onGroupEntrance " +
            "WHERE u.id=:userId")
    void updateNotificationSetting(
            boolean onDueDate, boolean onMyTaskCreation,
            boolean onMyTaskChange, boolean onGroupEntrance,
            long userId
    );
}
