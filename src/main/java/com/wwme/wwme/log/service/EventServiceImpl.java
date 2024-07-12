package com.wwme.wwme.log.service;

import com.wwme.wwme.group.domain.Group;
import com.wwme.wwme.group.repository.GroupRepository;
import com.wwme.wwme.log.domain.DTO.*;
import com.wwme.wwme.log.domain.Event;
import com.wwme.wwme.log.repository.EventRepository;
import com.wwme.wwme.user.domain.User;
import com.wwme.wwme.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;
    private final GroupRepository groupRepository;
    private final UserRepository userRepository;
    @Override
    public EventDTO createEvent(EventDTO eventDTO) {
        Event event = Event.builder()
                .user(eventDTO.getUser())
                .operationString(eventDTO.getOperationString())
                .operationTypeEnum(eventDTO.getOperationTypeEnum())
                .operationTime(eventDTO.getOperationTime())
                .build();
        Event savedEvent = eventRepository.save(event);

        EventDTO returnEventDTO = EventDTOFactory.createEventDTO(savedEvent);
        return eventDTO;
    }

    //TODO: check what happens when there is no matching group?
    public List<EventDTO> readGroupEvents(Group group) {
        groupRepository.findById(group.getId())
                .orElseThrow(()->new NoSuchElementException("Could not find group of ID: "
                +group.getId() + " in function readGroupEvents"));

        List<Event> eventList= eventRepository.findByGroupId(group.getId());
        List<EventDTO> eventDTOList = new ArrayList<>();
        for(Event e : eventList){
            eventDTOList.add(EventDTOFactory.createEventDTO(e));
        }

        return eventDTOList;
    }

    @Override
    public List<EventDTO> readGroupEventsPaging(Group group, Long last_id) {

        checkGroupExistanceDB(group);

        Pageable pageable = PageRequest.of(0,20);

        List<Event> eventList= eventRepository.findByGroupIdPaging(group.getId(),last_id,pageable);
        List<EventDTO> eventDTOList = new ArrayList<>();
        for(Event e : eventList){
            eventDTOList.add(EventDTOFactory.createEventDTO(e));
        }

        return eventDTOList;
    }

    private boolean checkGroupExistanceDB(Group group){
        groupRepository.findById(group.getId())
                .orElseThrow(()->new NoSuchElementException("Could not find group of ID: "
                        +group.getId() + " in function readGroupEvents"));
    }


}
