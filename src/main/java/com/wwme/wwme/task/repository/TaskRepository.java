package com.wwme.wwme.task.repository;

import com.wwme.wwme.task.domain.Task;
import com.wwme.wwme.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

    @Query("SELECT t FROM Task t LEFT JOIN t.userTaskList ut WHERE (t.group.id = :group_id)" +
            "AND (:user_id IS NULL OR ut.user.id = :user_id)" +
            "AND (:tag_id IS NULL OR t.tag.id = :tag_id)" +
            "AND (:start_time IS NULL OR t.startTime >= :start_time)" +
            "AND (:end_time IS NULL OR t.endTime <= :end_time)" +
            "AND (:is_done IS NULL OR t.totalIsDone = :is_done)")
    List<Task> findTasksByGroupAndFilters(@Param("group_id") Long group_id,
                                          @Param("user_id") Long user_id,
                                          @Param("tag_id") Long tag_id,
                                          @Param("start_time") LocalDateTime start_time,
                                          @Param("end_time") LocalDateTime end_time,
                                          @Param("is_done") Boolean is_done);

    @Query("SELECT t FROM Task t LEFT JOIN t.userTaskList ut WHERE (ut.user.id = :user_id)" +
            "AND (:tag_id IS NULL OR t.tag.id = :tag_id)" +
            "AND (:start_time IS NULL OR t.startTime >= :start_time)" +
            "AND (:end_time IS NULL OR t.endTime <= :end_time)" +
            "AND (:is_done IS NULL OR t.totalIsDone = :is_done)")
    List<Task> findTaskByUserIdAndFilters(@Param("user_id") Long user_id,
                                          @Param("tag_id") Long tag_id,
                                          @Param("start_time") LocalDateTime start_time,
                                          @Param("end_time") LocalDateTime end_time,
                                          @Param("is_done") Boolean is_done);
    @Query("SELECT t FROM Task t WHERE t.endTime >= :startDate AND t.endTime <= :endDate")
    List<Task> findTasksBetweenDates(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);

    List<Task> findAllByUserAndDate(User user, LocalDate date);

    @Query("SELECT t FROM Task t LEFT JOIN FETCH t.userTaskList WHERE t.id = :id")
    Optional<Task> findByIdWithUserTaskList(Long id);

    @Query("SELECT t FROM Task t LEFT JOIN FETCH t.userTaskList ut WHERE ut.user.id = :userId")
    List<Task> findTasksByUserIdFetchUserTask(@Param("userId") Long userId);
}
