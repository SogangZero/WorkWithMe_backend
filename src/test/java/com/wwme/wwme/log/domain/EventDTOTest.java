package com.wwme.wwme.log.domain;

import com.wwme.wwme.group.domain.Group;
import com.wwme.wwme.log.domain.DTO.CreateTaskLogDTO;
import com.wwme.wwme.task.domain.Task;
import com.wwme.wwme.user.domain.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class EventDTOTest {
    private Task mockTask;
    private Group mockGroup;
    private User mockUser;



    @BeforeEach
    void setup(){
        mockUser = mock(User.class);
        mockGroup = mock(Group.class);
        mockTask = mock(Task.class);
    }

    @Test
    void convert_Event_to_CreateTaskLogDTO_Test(){

    }


    @Test
    void createTaskLogDTO_build_withoutID_test(){
        when(mockTask.getTaskName()).thenReturn("Task1");
        when(mockUser.getNickname()).thenReturn("User1");
        when(mockGroup.getGroupName()).thenReturn("Group1");

        String operationString = "Task1";
        LocalDateTime now = LocalDateTime.now();

        CreateTaskLogDTO createTaskLogDTO = CreateTaskLogDTO.buildWithSpecificParamsNoID()
                .task(mockTask)
                .user(mockUser)
                .group(mockGroup)
                .operationTime(now)
                .operationTypeEnum(OperationType.CREATE_TASK)
                .newTaskName(mockTask.getTaskName())
                .build();

        assertEquals(createTaskLogDTO.getTask().getTaskName(),mockTask.getTaskName());
        assertEquals(createTaskLogDTO.getUser().getNickname(),mockUser.getNickname());
        assertEquals(createTaskLogDTO.getGroup().getGroupName(),mockGroup.getGroupName());
        assertEquals(createTaskLogDTO.getOperationTime(),now);
        assertEquals(createTaskLogDTO.getOperationTypeEnum(),OperationType.CREATE_TASK);
        assertEquals(operationString,createTaskLogDTO.getOperationString());
        assertEquals(mockTask.getTaskName(),createTaskLogDTO.getNewTaskName());

    }


    @Test
    void createTaskLogDTO_builder_withID_test(){
        String operationString = "Task1";
        LocalDateTime now = LocalDateTime.now();

        when(mockTask.getTaskName()).thenReturn("Task1");
        when(mockUser.getNickname()).thenReturn("User1");
        when(mockGroup.getGroupName()).thenReturn("Group1");

        CreateTaskLogDTO createTaskLogDTO =  CreateTaskLogDTO.buildWithOperationStringID()
                .id(1L)
                .task(mockTask)
                .user(mockUser)
                .group(mockGroup)
                .operationString(operationString)
                .operationTime(now)
                .operationTypeEnum(OperationType.CREATE_TASK)
                .build();

        assertEquals(createTaskLogDTO.getId(),1L);
        assertEquals(createTaskLogDTO.getTask().getTaskName(),mockTask.getTaskName());
        assertEquals(createTaskLogDTO.getUser().getNickname(),mockUser.getNickname());
        assertEquals(createTaskLogDTO.getGroup().getGroupName(),mockGroup.getGroupName());
        assertEquals(createTaskLogDTO.getOperationTime(),now);
        assertEquals(createTaskLogDTO.getOperationTypeEnum(),OperationType.CREATE_TASK);
        assertEquals(operationString,createTaskLogDTO.getOperationString());
    }

    void convert_CreateTaskLogDTO_To_Event_Test(){

    }
}
