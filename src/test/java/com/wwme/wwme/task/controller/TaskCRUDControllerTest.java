package com.wwme.wwme.task.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.wwme.wwme.group.domain.Group;
import com.wwme.wwme.group.domain.UserGroup;
import com.wwme.wwme.login.config.ResolverConfig;
import com.wwme.wwme.login.config.SecurityTestConfig;
import com.wwme.wwme.task.domain.DTO.receiveDTO.CreateTaskReceiveDTO;
import com.wwme.wwme.task.domain.DTO.receiveDTO.UpdateTaskReceiveDTO;
import com.wwme.wwme.task.domain.DTO.sendDTO.CUTaskSendDTO;
import com.wwme.wwme.task.domain.DTO.sendDTO.DataResponseDTO;
import com.wwme.wwme.task.domain.Tag;
import com.wwme.wwme.task.domain.Task;
import com.wwme.wwme.task.domain.UserTask;
import com.wwme.wwme.task.service.TaskCRUDService;
import com.wwme.wwme.task.service.TaskDTOBinder;
import com.wwme.wwme.user.domain.User;
import com.wwme.wwme.user.repository.UserRepository;
import com.wwme.wwme.user.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.LocalDateTime;
import java.util.ArrayList;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TaskCRUDController.class)
@Import({SecurityTestConfig.class, ResolverConfig.class})
@WithMockUser(username = "test", roles = "USER")
public class TaskCRUDControllerTest {

    @Autowired private MockMvc mvc;

    @MockBean private TaskCRUDService taskCRUDService;

    @MockBean private UserService userService;

    @MockBean private UserRepository userRepository;

    @MockBean private TaskDTOBinder taskDTOBinder;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    public void init() {
        objectMapper.registerModule(new JavaTimeModule());
    }
    @Test
    @DisplayName("/task [POST] - 올바른 인자가 넘어오고, task가 정상적으로 저장")
    public void createTaskNormal() throws Exception {
        String taskName = "test_task";
        LocalDateTime endTime = LocalDateTime.MAX;
        String type = "personal";
        Long tagId = 1L;
        Long groupId = 1L;
        Long todoUserId = 1L;

        //given
        User user = User.builder()
                .id(1L)
                .nickname("test")
                .registerDate(LocalDateTime.now())
                .userKey("test")
                .socialProvider("test")
                .role("ROLE_USER")
                .profileImageId(1L)
                .userGroup(new ArrayList<>())
                .build();

        Group group = Group.builder()
                .id(1L)
                .groupName("testGroup")
                .userGroupList(new ArrayList<>())
                .build();

        Tag tag = Tag.builder()
                .id(1L)
                .tagName("testTag")
                .group(group)
                .taskList(new ArrayList<>())
                .build();

        UserGroup userGroup = UserGroup.builder()
                .id(1L)
                .color(1L)
                .user(user)
                .group(group)
                .build();

        CreateTaskReceiveDTO taskDTO = CreateTaskReceiveDTO.builder()
                .task_name(taskName)
                .end_time(endTime)
                .task_type(type)
                .tag_id(tagId)
                .group_id(groupId)
                .todo_user_id(todoUserId)
                .build();

        Task task = Task.builder()
                .id(1L)
                .taskName(taskName)
                .startTime(LocalDateTime.now())
                .endTime(endTime)
                .taskType(type)
                .totalIsDone(false)
                .tag(tag)
                .group(group)
                .build();

        UserTask userTask = UserTask.builder()
                .id(1L)
                .user(user)
                .task(task)
                .isDone(false)
                .build();

        user.getUserTaskList().add(userTask);
        user.getUserGroup().add(userGroup);
        tag.addToTaskList(task);
        task.addUserTask(userTask);
        group.addUserGroup(userGroup);


        CUTaskSendDTO cuTaskSendDTO = new CUTaskSendDTO();
        DataResponseDTO dataResponseDTO = new DataResponseDTO(cuTaskSendDTO);
        String result = objectMapper.writeValueAsString(dataResponseDTO);
        String body = objectMapper.writeValueAsString(taskDTO);

        //when
        when(taskCRUDService.createTask(any(), any(), any(), any(), any(), any(), any()))
                .thenReturn(task);

        //then
        mvc.perform(MockMvcRequestBuilders.post("/task")
                        .contentType("application/json")
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"));
    }

    @Test
    @DisplayName("/task [POST] - 올바른 인자가 넘어오고, task가 저장되지 않으면 exception을 받음")
    public void createTaskReceiveException() throws Exception {
        //given
        String taskName = "test_task";
        LocalDateTime endTime = LocalDateTime.MAX;
        String type = "personal";
        Long tagId = 1L;
        Long groupId = 1L;
        Long todoUserId = 1L;
        CreateTaskReceiveDTO taskDTO = CreateTaskReceiveDTO.builder()
                .task_name(taskName)
                .end_time(endTime)
                .task_type(type)
                .tag_id(tagId)
                .group_id(groupId)
                .todo_user_id(todoUserId)
                .build();
        String body = objectMapper.writeValueAsString(taskDTO);

        //when
        when(taskCRUDService.createTask(any(), any(), any(), any(), any(), any(), any()))
                .thenThrow(IllegalArgumentException.class);
        //then

        mvc.perform(MockMvcRequestBuilders.post("/task")
                        .contentType("application/json")
                        .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType("application/json")
                );
    }

    @Test
    @DisplayName("/task [PUT] - 올바른 인자가 넘어오고, task가 정상적으로 업데이트됨")
    public void updateTaskNormal() throws Exception {
        Long taskId = 1L;
        String taskName = "updatedTask";
        LocalDateTime endTime = LocalDateTime.MAX;
        String type = "personal";
        Long tagId = 1L;
        Long todoUserId = 1L;

        //given
        User user = User.builder()
                .id(1L)
                .nickname("test")
                .registerDate(LocalDateTime.now())
                .userKey("test")
                .socialProvider("test")
                .role("ROLE_USER")
                .profileImageId(1L)
                .userGroup(new ArrayList<>())
                .build();

        Group group = Group.builder()
                .id(1L)
                .groupName("testGroup")
                .userGroupList(new ArrayList<>())
                .build();

        Tag tag = Tag.builder()
                .id(1L)
                .tagName("testTag")
                .group(group)
                .taskList(new ArrayList<>())
                .build();

        UserGroup userGroup = UserGroup.builder()
                .id(1L)
                .color(1L)
                .user(user)
                .group(group)
                .build();

        UpdateTaskReceiveDTO updateTaskDTO = UpdateTaskReceiveDTO.builder()
                .task_id(taskId)
                .end_time(endTime)
                .task_type(type)
                .tag_id(tagId)
                .todo_user_id(todoUserId)
                .build();

        Task task = Task.builder()
                .id(taskId)
                .taskName(taskName)
                .startTime(LocalDateTime.now())
                .endTime(endTime)
                .taskType(type)
                .totalIsDone(false)
                .tag(tag)
                .group(group)
                .build();

        CUTaskSendDTO cuTaskSendDTO = new CUTaskSendDTO();
        DataResponseDTO dataResponseDTO = new DataResponseDTO(cuTaskSendDTO);
        String result = objectMapper.writeValueAsString(dataResponseDTO);
        String body = objectMapper.writeValueAsString(updateTaskDTO);

        //when
        when(taskCRUDService.updateTask(any(), any(), any(), any(), any(), any()))
                .thenReturn(task);

        //then
        mvc.perform(MockMvcRequestBuilders.put("/task")
                        .contentType("application/json")
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"));
    }

    @Test
    @DisplayName("/task [PUT] - 올바른 인자가 넘어오고, task가 업데이트되지 않으면 exception을 받음")
    public void updateTaskReceiveException() throws Exception {
        //given
        Long taskId = 1L;
        String taskName = "updateTask";
        LocalDateTime endTime = LocalDateTime.MAX;
        String type = "personal";
        Long tagId = 1L;
        Long todoUserId = 1L;

        UpdateTaskReceiveDTO updateTaskDTO = UpdateTaskReceiveDTO.builder()
                .task_id(taskId)
                .end_time(endTime)
                .task_type(type)
                .tag_id(tagId)
                .todo_user_id(todoUserId)
                .build();
        String body = objectMapper.writeValueAsString(updateTaskDTO);

        //when
        when(taskCRUDService.updateTask(any(), any(), any(), any(), any(), any()))
                .thenThrow(IllegalArgumentException.class);

        //then
        mvc.perform(MockMvcRequestBuilders.put("/task")
                        .contentType("application/json")
                        .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType("application/json"));
    }
}
