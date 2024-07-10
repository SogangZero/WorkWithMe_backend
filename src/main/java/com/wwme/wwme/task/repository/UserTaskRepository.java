package com.wwme.wwme.task.repository;

import com.wwme.wwme.task.domain.Task;
import com.wwme.wwme.task.domain.UserTask;
import com.wwme.wwme.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;

@Repository
public interface UserTaskRepository extends JpaRepository<UserTask,Long> {
    @Modifying
    @Query("DELETE FROM UserTask t " +
            "WHERE t.task = :task")
    void deleteByTask(@Param("task") Task task);

    @Modifying
    @Query("DELETE FROM UserTask t " +
            "WHERE t.task = :task " +
            "AND t.user != :exceptUser")
    void deleteByTaskExceptForOnePerson(@Param("task") Task task,
                                          @Param("exceptUser") User exceptUser);

    @Query("SELECT ut from UserTask ut " +
            "LEFT JOIN Task t ON ut.task=t " +
            "WHERE DATE(t.endTime)=:endTime ")
    Collection<UserTask> findAllByEndTime(LocalDate endTime);
}
