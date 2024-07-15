package com.wwme.wwme.task.repository;

import com.wwme.wwme.group.domain.Group;
import com.wwme.wwme.task.domain.Task;
import com.wwme.wwme.user.domain.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Collection;
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

    @Query("SELECT t FROM Task t " +
            "LEFT JOIN FETCH t.userTaskList ut " +
            "WHERE ut.user.id = :userId " +
            "AND t.endTime between :startTime AND :endTime")
    List<Task> findAllByUserAndStartEndTimes(Long userId, LocalDateTime startTime, LocalDateTime endTime);


    @Query("SELECT t FROM Task t " +
            "LEFT JOIN FETCH t.userTaskList ut " +
            "LEFT JOIN FETCH ut.user u " +
            "LEFT JOIN FETCH t.tag tg " +
            "LEFT JOIN FETCH t.group g " +
            "WHERE t.id = :taskId")
    Optional<Task> findTaskByIdWithUserTaskList(Long taskId);

    @Query("SELECT t FROM Task t " +
            "LEFT JOIN FETCH t.userTaskList ut " +
            "LEFT JOIN FETCH t.group gp " +
            "LEFT JOIN FETCH t.tag tg " +
            "WHERE ut.user.id = :userId " +
            "AND t.totalIsDone = false "+
            "AND ut.isDone = false " +
            "AND t.endTime >= :endTime " +
            "AND (:last_task_id IS NULL OR (t.endTime > :endTime) OR (t.endTime = :endTime AND t.id > :last_task_id)) " +
            "ORDER BY t.endTime asc, t.id asc")
    List<Task> findTasksByUserIdFetchUserTask(@Param("userId") Long userId, @Param("endTime") LocalDateTime endTime,
                                              @Param("last_task_id") Long last_task_id, Pageable pageable);

    @Query("SELECT t FROM Task t " +
            "LEFT JOIN FETCH t.userTaskList ut " +
            "LEFT JOIN FETCH t.group gp " +
            "LEFT JOIN FETCH t.tag tg " +
            "WHERE gp.id = :groupId " +
            "AND (:user is NULL or ut.user = :user) " +
            "AND (:totalIsDone IS NULL or t.totalIsDone = :totalIsDone) " +
            "AND t.endTime >= :startDate " +
            "AND t.endTime <= :endDate " +
            "AND ((:allTags=TRUE) OR (:containNoTagTask=TRUE AND tg.id IS NULL) OR tg.id in :tagList) " +
            "AND (:lastId IS NULL OR (t.endTime > :lastEndTime) OR (t.endTime = :lastEndTime AND t.id > :lastId)) " +
            "ORDER BY t.endTime asc, t.id asc"
    )
    List<Task> findAllByGroupWithArguments(
            @Param("lastId") Long lastId,
            @Param("lastEndTime") LocalDateTime lastEndTime,
            @Param("groupId") Long groupId,
            @Param("user") User user,
            @Param("totalIsDone") Boolean totalIsDone,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            @Param("tagList") List<Long> tagList,
            @Param("allTags") boolean allTags,
            @Param("containNoTagTask") boolean containNoTagTask,
            Pageable pageable
    );

    @Query("SELECT t from Task t " +
            "LEFT JOIN FETCH t.userTaskList ut " +
            "WHERE t.id = :taskId")
    Optional<Task> findByTaskIdWithUserList(Long taskId);

    void deleteByGroup(Group group);
}
