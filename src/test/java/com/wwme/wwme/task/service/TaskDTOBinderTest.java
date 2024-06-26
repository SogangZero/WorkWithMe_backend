package com.wwme.wwme.task.service;

import com.wwme.wwme.group.domain.Group;
import com.wwme.wwme.task.domain.DTO.sendDTO.CUTaskSendDTO;
import com.wwme.wwme.task.domain.Tag;
import com.wwme.wwme.task.domain.Task;
import com.wwme.wwme.task.domain.UserTask;
import com.wwme.wwme.task.repository.TaskRepository;
import com.wwme.wwme.user.domain.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class TaskDTOBinderTest {
    @Mock
    private TaskRepository taskRepository;

    @InjectMocks
    private TaskDTOBinder taskDTOBinder;

    private Task task;
    private Group group;
    private Tag tag;
    private User user;
    private UserTask userTask;

    @BeforeEach
    void init() {
        MockitoAnnotations.openMocks(this);

        group = new Group();
        group.setId(1L);
        group.setGroupName("Test Group");

        tag = new Tag();
        tag.setId(1L);
        tag.setTagName("Test Tag");

        user = new User();
        user.setId(1L);
        user.setNickname("testuser");
        user.setProfileImageId(1L);

        userTask = new UserTask();
        userTask.setUser(user);
        userTask.setTask(task);
        userTask.setIsDone(true);

        List<UserTask> userTaskList = new ArrayList<>();
        userTaskList.add(userTask);

        task = Task.builder()
                .id(1L)
                .taskName("Test Task")
                .group(group)
                .tag(tag)
                .userTaskList(userTaskList)
                .taskType("personal")
                .startTime(LocalDateTime.now())
                .endTime(LocalDateTime.MAX)
                .build();
    }

    @Test
    @DisplayName("bindCUTaskSendDTO - Task를 CUTaskSendDTO로 변환")
    void bindCUTaskSendDTOTest() {
        //when
        CUTaskSendDTO dto = taskDTOBinder.bindCUTaskSendDTO(task,user);

        //then
        assertThat(dto.getTask_id()).isEqualTo(task.getId());
        assertThat(dto.getTask_name()).isEqualTo(task.getTaskName());
        assertThat(dto.getGroup().getGroup_id()).isEqualTo(group.getId());
        assertThat(dto.getTag().getTag_id()).isEqualTo(tag.getId());
        assertThat(dto.getIs_done_count()).isEqualTo(1);
        assertThat(dto.getIs_done_personal()).isTrue();
        assertThat(dto.getIs_done_total()).isTrue();
        assertThat(dto.getUser_list()).hasSize(1);
        assertThat(dto.getUser_list().get(0).getUser_id()).isEqualTo(user.getId());
        assertThat(dto.getTask_type()).isEqualTo(task.getTaskType());
        assertThat(dto.getStart_time()).isEqualTo(task.getStartTime());
        assertThat(dto.getEnd_time()).isEqualTo(task.getEndTime());
    }
}
