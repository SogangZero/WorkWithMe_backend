package com.wwme.wwme.group.repository;

import com.wwme.wwme.group.domain.Group;
import com.wwme.wwme.group.domain.UserGroup;
import com.wwme.wwme.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserGroupRepository extends JpaRepository<UserGroup, Long> {
    Optional<UserGroup> findByUserAndGroup(User user, Group group);
}
