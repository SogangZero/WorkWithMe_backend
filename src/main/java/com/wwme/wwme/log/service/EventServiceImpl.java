package com.wwme.wwme.log.service;

import com.wwme.wwme.group.domain.Group;
import com.wwme.wwme.group.repository.GroupRepository;
import com.wwme.wwme.log.domain.DTO.*;
import com.wwme.wwme.log.domain.Event;
import com.wwme.wwme.log.repository.EventRepository;
import com.wwme.wwme.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;
    private final GroupRepository groupRepository;
    @Override
    public EventDTO createEvent(EventDTO eventDTO) {
        Event event = Event.builder()
                .user(eventDTO.getUser())
                .operationString(eventDTO.getOperationString())
                .operationTypeEnum(eventDTO.getOperationTypeEnum())
                .operationTime(eventDTO.getOperationTime())
                .build();

        Event newEvent = eventRepository.save(event);


    }

    //TODO: check what happens when there is no matching group?
    public List<EventDTO> readGroupEvents(Group group) {
        List<Event> eventList= eventRepository.findByGroupId(group.getId());

        List<EventDTO> eventDTOList = new ArrayList<>();

        for(Event e : eventList){
            if(e.getGroup() == null || groupRepository.existsById(group.getId())){
                //The group does not exist
            }else{

            }
        }
        return null;
    }

    @Override
    public List<EventDTO> readUserEvents(User user) {
        return null;
    }


    private EventDTO convertToDTO(Event event){
        EventDTO.EventDTOBuilder builder = switch (event.getOperationTypeEnum()) {
            case CREATE_TASK -> CreateTaskLogDTO.builder();
            case DELETE_TASK -> DeleteTaskLogDTO.builder();
            case UPDATE_TASK_CHANGE_TAG -> UpdateTaskChangeTagDTO.builder();
            case UPDATE_TASK_NAME -> UpdateTaskNameLogDTO.builder();
            case UPDATE_TASK_DELETE_TAG -> UpdateTaskDeleteTagDTO.builder();
            case UPDATE_TASK_DUE_DATE -> UpdateTaskDueDateLogDTO.builder();
            case UPDATE_TASK_TYPE -> UpdateTaskTypeLogDTO.builder();
            default ->
                    throw new IllegalArgumentException("operationTypeEnum Error : unsupported enum : " + event.getOperationTypeEnum());
        };

        return builder
                .id(event.getId())
                .operationTime(event.getOperationTime())
                .operationTypeEnum(event.getOperationTypeEnum())
                .group(event.getGroup())
                .user(event.getUser())
                .operationString(event.getOperationString())
                .build();
    }

}
