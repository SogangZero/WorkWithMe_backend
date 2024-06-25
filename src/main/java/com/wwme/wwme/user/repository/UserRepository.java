package com.wwme.wwme.user.repository;

import com.wwme.wwme.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

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
}
