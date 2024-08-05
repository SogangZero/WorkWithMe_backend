package com.wwme.wwme.log.repository;

import com.wwme.wwme.log.domain.Event;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EventRepository extends JpaRepository<Event,Long> {


    List<Event> findByGroupId(Long groupId);

    @Query("SELECT e FROM Event e WHERE e.group.id = :groupId AND e.id > :lastId " +
            "ORDER BY e.operationTime asc ")
    List<Event> findByGroupIdPagingWithLastId(
            @Param("groupId") Long groupId,
            @Param("lastId") Long lastId,
            Pageable pageable);

    @Query("SELECT e FROM Event e WHERE e.group.id = :groupId ORDER BY e.operationTime asc ")
    List<Event> findByGroupIdPagingWithoutLastId(
            @Param("groupId") Long groupId,
            Pageable pageable);
}


