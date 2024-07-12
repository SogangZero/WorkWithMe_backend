package com.wwme.wwme.log.service;

import com.wwme.wwme.group.domain.Group;
import com.wwme.wwme.group.repository.GroupRepository;
import com.wwme.wwme.log.domain.DTO.*;
import com.wwme.wwme.log.domain.Event;
import com.wwme.wwme.log.repository.EventRepository;
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


    @Override
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
    public List<EventDTO> readGroupEventsPaging(ReceiveLogDTO receiveLogDTO) {

        //check Group Id integrity
        checkGroupExistanceDB(receiveLogDTO.getGroup_id());

        //List to store Event entities from DB
        List<Event> eventList;

        //Pageable
        Pageable pageable = PageRequest.of(0, 10);

        if(receiveLogDTO.getLast_id() == null){ //start from first
            eventList = eventRepository.findByGroupIdPagingWithoutLastId(
                    receiveLogDTO.getGroup_id(),
                    pageable
            );
        }else{
            eventList = eventRepository.findByGroupIdPagingWithLastId(
                    receiveLogDTO.getGroup_id(),
                    receiveLogDTO.getLast_id(),
                    pageable
            );
        }

        List<EventDTO> eventDTOList = new ArrayList<>();
        for(Event e : eventList){
            eventDTOList.add(EventDTOFactory.createEventDTO(e));
        }

        return eventDTOList;
    }

    private void checkGroupExistanceDB(Long groupId){
        groupRepository.findById(groupId)
                .orElseThrow(()->new NoSuchElementException("Could not find group of ID: "
                        +groupId + " in function readGroupEvents"));
    }


}
