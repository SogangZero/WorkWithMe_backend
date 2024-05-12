package com.wwme.wwme.task.repository;

import com.wwme.wwme.task.domain.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

    @Query("SELECT t FROM Task t JOIN t.userTaskList ut WHERE (t.group.id = :group_id)" +
            "AND (:user_id IS NULL OR ut.user.id = :user_id)" +
            "AND (:tag_id IS NULL OR t.tag.id = :tag_id)" +
            "AND (:start_time IS NULL OR t.start_time >= :start_time)" +
            "AND (:end_time IS NULL OR t.end_time <= :end_time)" +
            "AND (:is_done IS NULL OR t.total_is_done = :is_done)")
    List<Task> findTasksByGroupAndFilters(@Param("group_id") Long group_id,
                                          @Param("user_id") Long user_id,
                                          @Param("tag_id") Long tag_id,
                                          @Param("start_time") LocalDateTime start_time,
                                          @Param("end_time") LocalDateTime end_time,
                                          @Param("is_done") Boolean is_done);

    @Query("SELECT t FROM Task t JOIN t.userTaskList ut WHERE (ut.user.id = :user_id)" +
            "AND (:tag_id IS NULL OR t.tag.id = :tag_id)" +
            "AND (:start_time IS NULL OR t.start_time >= :start_time)" +
            "AND (:end_time IS NULL OR t.end_time <= :end_time)" +
            "AND (:is_done IS NULL OR t.total_is_done = :is_done)")
    List<Task> findTaskByUserIdAndFilters(@Param("user_id") Long user_id,
                                          @Param("tag_id") Long tag_id,
                                          @Param("start_time") LocalDateTime start_time,
                                          @Param("end_time") LocalDateTime end_time,
                                          @Param("is_done") Boolean is_done);
}
