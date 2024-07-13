package com.wwme.wwme.log.service;

import com.wwme.wwme.group.domain.Group;
import com.wwme.wwme.log.domain.DTO.EventDTO;
import com.wwme.wwme.log.domain.DTO.ReceiveLogDTO;
import com.wwme.wwme.user.domain.User;

import java.util.List;

public interface EventService {

    /**
     * Save an event to the DB
     * @param eventDTO : receives a subclass of EventDTO
     */
    EventDTO createEvent(EventDTO eventDTO);



    /**
     * @param receiveLogDTO
     * Read All events of operations conducted
     * from a specific group, paging included
     */
    List<EventDTO> readGroupEventsPaging(ReceiveLogDTO receiveLogDTO);
}
