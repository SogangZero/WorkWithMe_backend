package com.wwme.wwme.group.repository;

import com.wwme.wwme.group.domain.Group;
import com.wwme.wwme.task.domain.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.Optional;


@Repository
public interface GroupRepository extends JpaRepository<Group, Long> {

    @Query("SELECT g from Group g " +
            "JOIN fetch g.userGroupList ug " +
            "WHERE g.id = :groupId")
    Optional<Group> findGroupByIdLoadUserTaskList(Long groupId);


    @Query("SELECT g FROM Group g " +
            "WHERE g.userGroupList IS EMPTY")
    Collection<Group> findEmptyGroups();
}
