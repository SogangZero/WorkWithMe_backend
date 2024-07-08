package com.wwme.wwme.log.service;

import com.wwme.wwme.group.domain.Group;
import com.wwme.wwme.log.domain.DTO.EventDTO;
import com.wwme.wwme.log.domain.Event;
import com.wwme.wwme.user.domain.User;

import java.util.List;

public interface EventService {

    /**
     * Save an event to the DB
     * @param eventDTO
     */
    EventDTO createEvent(EventDTO eventDTO);

    /**
     * @param group
     * Read All events of operations conducted
     * from a specific group
     */
    List<EventDTO> readGroupEvents(Group group);

    /**
     * Read All events of operations
     * conducted by a specific User.
     * @param user
     */
    List<EventDTO> readUserEvents(User user);



}
