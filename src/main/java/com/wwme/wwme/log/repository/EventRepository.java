package com.wwme.wwme.log.repository;

import com.wwme.wwme.log.domain.Event;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EventRepository extends JpaRepository<Event,Long> {


    List<Event> findByGroupId(Long groupId);

    @Query("Select e from Event e WHERE " +
            "e.group.id = :groupId " +
            "AND e.id > :last_log_id ")
    List<Event> findByGroupIdPaging(Long groupId, Long last_log_id, Pageable pageable);
    List<Event> findByUserId(Long userId);

}
