package com.wwme.wwme.log.service;

import com.wwme.wwme.group.domain.Group;
import com.wwme.wwme.log.domain.DTO.EventDTO;
import com.wwme.wwme.user.domain.User;

import java.util.List;

public interface EventService {

    /**
     * Save an event to the DB
     * @param eventDTO : receives a subclass of EventDTO
     */
    EventDTO createEvent(EventDTO eventDTO);

    /**
     * @param group
     * Read All events of operations conducted
     * from a specific group
     */
    List<EventDTO> readGroupEvents(Group group);


    /**
     * @param group
     * Read All events of operations conducted
     * from a specific group, paging included
     */
    List<EventDTO> readGroupEventsPaging(Group group, Long last_id);
}
