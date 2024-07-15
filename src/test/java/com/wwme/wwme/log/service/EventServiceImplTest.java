package com.wwme.wwme.log.service;

import com.wwme.wwme.group.domain.Group;
import com.wwme.wwme.group.repository.GroupRepository;
import com.wwme.wwme.log.domain.DTO.CreateTaskLogDTO;
import com.wwme.wwme.log.domain.DTO.EventDTO;
import com.wwme.wwme.log.domain.DTO.UpdateTaskNameLogDTO;
import com.wwme.wwme.log.domain.Event;
import com.wwme.wwme.log.domain.OperationType;
import com.wwme.wwme.log.repository.EventRepository;
import com.wwme.wwme.task.domain.Task;
import com.wwme.wwme.user.domain.User;
import com.wwme.wwme.user.repository.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


public class EventServiceImplTest {

    @Mock
    private EventRepository eventRepository;
    @Mock
    private GroupRepository groupRepository;
    @Mock
    private UserRepository userRepository;
    @InjectMocks
    private EventServiceImpl eventService;

    private User mockUser;
    private Group mockGroup;
    private Task mockTask;

    @BeforeEach
    void setUp(){
        MockitoAnnotations.openMocks(this);
        mockUser = mock(User.class);
        mockGroup = mock(Group.class);
        mockTask = mock(Task.class);
    }

    @Test
    void TestCreateEvent_CreateTaskLogDTO(){
        //Arrange
        LocalDateTime now = LocalDateTime.now();
        when(mockTask.getTaskName()).thenReturn("Task1");

        CreateTaskLogDTO createTaskLogDTO = CreateTaskLogDTO.buildWithSpecificParamsNoID()
                .user(mockUser)
                .task(mockTask)
                .group(mockGroup)
                .operationTypeEnum(OperationType.CREATE_TASK)
                .newTaskName(mockTask.getTaskName())
                .operationTime(now)
                .build();

        Event event = Event.builder()
                .user(mockUser)
                .operationString(createTaskLogDTO.getOperationString())
                .operationTypeEnum(createTaskLogDTO.getOperationTypeEnum())
                .operationTime(createTaskLogDTO.getOperationTime())
                .build();

        when(eventRepository.save(any(Event.class))).thenReturn(event);

        //Act
        EventDTO result = eventService.createEvent(createTaskLogDTO);

        //Result
        Assertions.assertNotNull(result);
        assertEquals(createTaskLogDTO.getUser(), result.getUser());
        assertEquals(createTaskLogDTO.getOperationString(), result.getOperationString());
        assertEquals(createTaskLogDTO.getOperationTypeEnum(), result.getOperationTypeEnum());
        assertEquals(createTaskLogDTO.getOperationTime(), result.getOperationTime());
        assertEquals(createTaskLogDTO.getGroup(), result.getGroup());


    }

    @Test
    void TestCreateEvent_UpdateTaskNameDTO(){
        //Arrange
        LocalDateTime now = LocalDateTime.now();
        String afterTaskName = "afterTaskName";

        when(mockTask.getTaskName()).thenReturn("Task1");

        UpdateTaskNameLogDTO updateTaskNameLogDTO = UpdateTaskNameLogDTO.buildWithSpecificParamsNoID()
                .user(mockUser)
                .task(mockTask)
                .group(mockGroup)
                .operationTypeEnum(OperationType.CREATE_TASK)
                .beforeTaskName(mockTask.getTaskName())
                .afterTaskName(afterTaskName)
                .operationTime(now)
                .build();

        Event event = Event.builder()
                .user(mockUser)
                .operationString(updateTaskNameLogDTO.getOperationString())
                .operationTypeEnum(updateTaskNameLogDTO.getOperationTypeEnum())
                .operationTime(updateTaskNameLogDTO.getOperationTime())
                .build();

        when(eventRepository.save(any(Event.class))).thenReturn(event);

        //Act
        EventDTO result = eventService.createEvent(updateTaskNameLogDTO);

        //Result
        Assertions.assertNotNull(result);
        assertEquals(updateTaskNameLogDTO.getUser(), result.getUser());
        assertEquals(updateTaskNameLogDTO.getOperationString(), result.getOperationString());
        assertEquals(updateTaskNameLogDTO.getOperationTypeEnum(), result.getOperationTypeEnum());
        assertEquals(updateTaskNameLogDTO.getOperationTime(), result.getOperationTime());
        assertEquals(updateTaskNameLogDTO.getGroup(), result.getGroup());

    }

}
