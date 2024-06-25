package com.wwme.wwme.task.service;

import com.wwme.wwme.group.domain.Group;
import com.wwme.wwme.group.domain.UserGroup;
import com.wwme.wwme.group.repository.GroupRepository;
import com.wwme.wwme.login.config.ResolverConfig;
import com.wwme.wwme.login.config.SecurityTestConfig;
import com.wwme.wwme.task.domain.DTO.TaskDTO;
import com.wwme.wwme.task.domain.DTO.receiveDTO.CreateTaskReceiveDTO;
import com.wwme.wwme.task.domain.Tag;
import com.wwme.wwme.task.domain.Task;
import com.wwme.wwme.task.domain.UserTask;
import com.wwme.wwme.task.repository.TagRepository;
import com.wwme.wwme.task.repository.TaskRepository;
import com.wwme.wwme.task.repository.UserTaskRepository;
import com.wwme.wwme.user.domain.User;
import com.wwme.wwme.user.repository.UserRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@WebMvcTest(TaskCRUDServiceImpl.class)
@Import({SecurityTestConfig.class, ResolverConfig.class})
public class TaskCRUDServiceTest {
    @Autowired
    private TaskCRUDServiceImpl taskCRUDService;

    @MockBean
    private TagRepository tagRepository;

    @MockBean
    private GroupRepository groupRepository;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private TaskRepository taskRepository;

    @MockBean
    private UserTaskRepository userTaskRepository;

    private String taskName = "testTask";
    private String taskType = "personal";
    private Long tagId = 1L;
    private Long groupId = 1L;
    private Long todoUserId = 1L;
    private LocalDateTime endTime = LocalDateTime.MAX;

    private User user;
    private Group group;
    private Group notMatchGroup;
    private Tag tag;
    private UserGroup userGroup;
    private CreateTaskReceiveDTO taskDTO;
    private Task task;
    private UserTask userTask;



    @BeforeEach
    public void init() {
        user = User.builder()
                .id(1L)
                .nickname("test")
                .registerDate(LocalDateTime.now())
                .userKey("test")
                .socialProvider("test")
                .role("ROLE_USER")
                .profileImageId(1L)
                .userGroup(new ArrayList<>())
                .build();

        group = Group.builder()
                .id(1L)
                .groupName("testGroup")
                .userGroupList(new ArrayList<>())
                .build();

        notMatchGroup = Group.builder()
                .id(2L)
                .groupName("test2")
                .userGroupList(new ArrayList<>())
                .build();

        tag = Tag.builder()
                .id(1L)
                .tagName("testTag")
                .group(group)
                .taskList(new ArrayList<>())
                .build();

        userGroup = UserGroup.builder()
                .id(1L)
                .color(1L)
                .user(user)
                .group(group)
                .build();

        taskDTO = CreateTaskReceiveDTO.builder()
                .task_name(taskName)
                .end_time(endTime)
                .task_type(taskType)
                .tag_id(tagId)
                .group_id(groupId)
                .todo_user_id(todoUserId)
                .build();

        task = Task.builder()
                .id(1L)
                .taskName(taskName)
                .startTime(LocalDateTime.now())
                .endTime(endTime)
                .taskType(taskType)
                .totalIsDone(false)
                .tag(tag)
                .group(group)
                .build();

        userTask = UserTask.builder()
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
    }

    @Test
    @DisplayName("createTask - 정상적인 인자일 경우 task가 정상적으로 저장[Personal]")
    public void createTaskSuccessPersonal() throws Exception {
        //given

        //when
        when(tagRepository.findById(any()))
                .thenReturn(Optional.of(tag));
        when(groupRepository.findById(any()))
                .thenReturn(Optional.of(group));
        when(userRepository.findById(any()))
                .thenReturn(Optional.of(user));
        when(taskRepository.save(any()))
                .thenReturn(task);

        Task taskElement = null;
        try {
            taskElement = taskCRUDService.createTask(taskDTO.getTask_name(),
                    taskDTO.getEnd_time(),
                    taskDTO.getTask_type(),
                    taskDTO.getTag_id(),
                    taskDTO.getGroup_id(),
                    taskDTO.getTodo_user_id(),
                    user);
        } catch (Exception e) {
            fail("fail");
        }

        //then
        assertThat(taskElement.getId()).isEqualTo(task.getId());
    }

    @Test
    @DisplayName("createTask - 정상적인 인자일 경우 task가 정상적으로 저장[Group]")
    public void createTaskSuccessGroup() throws Exception {
        //given
        User sameGroupUser = User.builder()
                .id(1L)
                .nickname("test2")
                .registerDate(LocalDateTime.now())
                .userKey("test2")
                .socialProvider("test2")
                .role("ROLE_USER")
                .profileImageId(1L)
                .userGroup(new ArrayList<>())
                .build();

        UserGroup userGroup2 = UserGroup.builder()
                .id(2L)
                .user(sameGroupUser)
                .group(group)
                .color(1L)
                .build();

        sameGroupUser.getUserGroup().add(userGroup2);
        group.addUserGroup(userGroup2);

        //when
        when(tagRepository.findById(any()))
                .thenReturn(Optional.of(tag));
        when(groupRepository.findById(any()))
                .thenReturn(Optional.of(group));
        when(userRepository.findById(any()))
                .thenReturn(Optional.of(user));
        when(taskRepository.save(any()))
                .thenReturn(task);
        //then
        Task taskElement = null;
        try {
            taskElement = taskCRUDService.createTask(taskDTO.getTask_name(),
                    taskDTO.getEnd_time(),
                    "group",
                    taskDTO.getTag_id(),
                    taskDTO.getGroup_id(),
                    taskDTO.getTodo_user_id(),
                    user);
        } catch (Exception e) {
            fail("fail");
        }
    }

    @Test
    @DisplayName("createTask - 정상적인 인자일 경우 task가 정상적으로 저장[Anyone]")
    public void createTaskSuccessAnyone() throws Exception {
        task.changeTaskType("anyone");
        when(tagRepository.findById(any()))
                .thenReturn(Optional.of(tag));
        when(groupRepository.findById(any()))
                .thenReturn(Optional.of(group));
        when(userRepository.findById(any()))
                .thenReturn(Optional.of(user));
        when(taskRepository.save(any()))
                .thenReturn(task);

        Task taskElement = null;
        try {
            taskElement = taskCRUDService.createTask(taskDTO.getTask_name(),
                    taskDTO.getEnd_time(),
                    "anyone",
                    taskDTO.getTag_id(),
                    taskDTO.getGroup_id(),
                    taskDTO.getTodo_user_id(),
                    user);
        } catch (Exception e) {
            fail("fail");
        }

        //then
        assertThat(taskElement.getId()).isEqualTo(task.getId());
        assertThat(taskElement.getTaskType()).isEqualTo("anyone");
    }

    @Test
    @DisplayName("createTask - taskName이 빈 값인 경우 예외 발생")
    public void createTaskFailEmptyTaskName() throws Exception {
        //given
        CreateTaskReceiveDTO dto = CreateTaskReceiveDTO.builder()
                .task_name("")
                .end_time(endTime)
                .task_type(taskType)
                .tag_id(tagId)
                .group_id(groupId)
                .todo_user_id(todoUserId)
                .build();
        //when

        when(tagRepository.findById(any()))
                .thenReturn(Optional.of(tag));
        when(groupRepository.findById(any()))
                .thenReturn(Optional.of(group));
        when(userRepository.findById(any()))
                .thenReturn(Optional.of(user));
        when(taskRepository.save(any()))
                .thenReturn(task);

        //then

        assertThrows(IllegalArgumentException.class, () -> taskCRUDService.createTask(dto.getTask_name(),
                dto.getEnd_time(),
                dto.getTask_type(),
                dto.getTag_id(),
                dto.getGroup_id(),
                dto.getTodo_user_id(),
                user));
    }

    @Test
    @DisplayName("createTask - endTime이 저장하려는 시기보다 앞선 경우 예외 발생")
    public void createTaskFailEarlyEndTime() throws Exception {
        //given
        CreateTaskReceiveDTO dto = CreateTaskReceiveDTO.builder()
                .task_name(taskName)
                .end_time(LocalDateTime.MIN)
                .task_type(taskType)
                .tag_id(tagId)
                .group_id(groupId)
                .todo_user_id(todoUserId)
                .build();
        //when

        when(tagRepository.findById(any()))
                .thenReturn(Optional.of(tag));
        when(groupRepository.findById(any()))
                .thenReturn(Optional.of(group));
        when(userRepository.findById(any()))
                .thenReturn(Optional.of(user));
        when(taskRepository.save(any()))
                .thenReturn(task);

        //then

        assertThrows(IllegalArgumentException.class, () -> taskCRUDService.createTask(dto.getTask_name(),
                dto.getEnd_time(),
                dto.getTask_type(),
                dto.getTag_id(),
                dto.getGroup_id(),
                dto.getTodo_user_id(),
                user));
    }

    @Test
    @DisplayName("createTask - taskType이 지정되지 않은 경우 예외 발생")
    public void createTaskFailInvalidTaskType() throws Exception {
        //given
        CreateTaskReceiveDTO dto = CreateTaskReceiveDTO.builder()
                .task_name(taskName)
                .end_time(endTime)
                .task_type("unvalid")
                .tag_id(tagId)
                .group_id(groupId)
                .todo_user_id(todoUserId)
                .build();
        //when

        when(tagRepository.findById(any()))
                .thenReturn(Optional.of(tag));
        when(groupRepository.findById(any()))
                .thenReturn(Optional.of(group));
        when(userRepository.findById(any()))
                .thenReturn(Optional.of(user));
        when(taskRepository.save(any()))
                .thenReturn(task);

        //then

        assertThrows(IllegalArgumentException.class, () -> taskCRUDService.createTask(dto.getTask_name(),
                dto.getEnd_time(),
                dto.getTask_type(),
                dto.getTag_id(),
                dto.getGroup_id(),
                dto.getTodo_user_id(),
                user)
        );
    }

    @Test
    @DisplayName("createTask - 존재하지 않는 태그에 task를 넣으려 한다면 예외 발생")
    public void createTaskFailNotFoundTag() throws Exception {
        //given
        CreateTaskReceiveDTO dto = CreateTaskReceiveDTO.builder()
                .task_name(taskName)
                .end_time(endTime)
                .task_type(taskType)
                .tag_id(-1L)
                .group_id(groupId)
                .todo_user_id(todoUserId)
                .build();
        //when

        when(tagRepository.findById(any()))
                .thenThrow(IllegalArgumentException.class);
        when(groupRepository.findById(any()))
                .thenReturn(Optional.of(group));
        when(userRepository.findById(any()))
                .thenReturn(Optional.of(user));
        when(taskRepository.save(any()))
                .thenReturn(task);

        //then

        assertThrows(IllegalArgumentException.class, () -> taskCRUDService.createTask(dto.getTask_name(),
                dto.getEnd_time(),
                dto.getTask_type(),
                dto.getTag_id(),
                dto.getGroup_id(),
                dto.getTodo_user_id(),
                user)
        );
    }

    @Test
    @DisplayName("createTask - 존재하지 않는 그룹에 task를 넣으려 한다면 예외 발생")
    public void createTaskFailNotFoundGroup() throws Exception {
        //given
        CreateTaskReceiveDTO dto = CreateTaskReceiveDTO.builder()
                .task_name(taskName)
                .end_time(endTime)
                .task_type(taskType)
                .tag_id(tagId)
                .group_id(-1L)
                .todo_user_id(todoUserId)
                .build();
        //when

        when(tagRepository.findById(any()))
                .thenReturn(Optional.of(tag));
        when(groupRepository.findById(any()))
                .thenThrow(IllegalArgumentException.class);
        when(userRepository.findById(any()))
                .thenReturn(Optional.of(user));
        when(taskRepository.save(any()))
                .thenReturn(task);

        //then

        assertThrows(IllegalArgumentException.class, () -> taskCRUDService.createTask(dto.getTask_name(),
                dto.getEnd_time(),
                dto.getTask_type(),
                dto.getTag_id(),
                dto.getGroup_id(),
                dto.getTodo_user_id(),
                user)
        );
    }

    @Test
    @DisplayName("createTask - 존재하지 않는 todo User를 지정한다면 예외 발생")
    public void createTaskFail() throws Exception {
        //given
        CreateTaskReceiveDTO dto = CreateTaskReceiveDTO.builder()
                .task_name(taskName)
                .end_time(endTime)
                .task_type(taskType)
                .tag_id(tagId)
                .group_id(groupId)
                .todo_user_id(-1L)
                .build();
        //when

        when(tagRepository.findById(any()))
                .thenReturn(Optional.of(tag));
        when(groupRepository.findById(any()))
                .thenReturn(Optional.of(group));
        when(userRepository.findById(any()))
                .thenThrow(IllegalArgumentException.class);
        when(taskRepository.save(any()))
                .thenReturn(task);

        //then

        assertThrows(IllegalArgumentException.class, () -> taskCRUDService.createTask(dto.getTask_name(),
                dto.getEnd_time(),
                dto.getTask_type(),
                dto.getTag_id(),
                dto.getGroup_id(),
                dto.getTodo_user_id(),
                user)
        );
    }

    @Test
    @DisplayName("createTask - user가 속하지 않는 그룹에 넣으려 한다면 예외 발생")
    public void createTaskFailInvalidGroup() throws Exception {
        //given
        User todoUser = User.builder()
                .id(2L)
                .nickname("test")
                .registerDate(LocalDateTime.now())
                .userKey("test")
                .socialProvider("test")
                .role("ROLE_USER")
                .profileImageId(1L)
                .userGroup(new ArrayList<>())
                .build();


        CreateTaskReceiveDTO dto = CreateTaskReceiveDTO.builder()
                .task_name(taskName)
                .end_time(endTime)
                .task_type(taskType)
                .tag_id(tagId)
                .group_id(notMatchGroup.getId())
                .todo_user_id(todoUserId)
                .build();

        tag.setGroup(notMatchGroup);
        //when

        when(tagRepository.findById(any()))
                .thenReturn(Optional.of(tag));
        when(groupRepository.findById(any()))
                .thenReturn(Optional.of(notMatchGroup));
        when(userRepository.findById(any()))
                .thenReturn(Optional.of(user));
        when(taskRepository.save(any()))
                .thenReturn(task);

        //then

        assertThrows(IllegalArgumentException.class, () -> taskCRUDService.createTask(dto.getTask_name(),
                dto.getEnd_time(),
                dto.getTask_type(),
                dto.getTag_id(),
                dto.getGroup_id(),
                dto.getTodo_user_id(),
                user)
        );
    }

    @Test
    @DisplayName("createTask - user가 본인 그룹에 속하지 않는 tag에 넣으려 한다면 예외 발생")
    public void createTaskFailInvalidGroupAndTag() throws Exception {
        // given
        Tag tag = Tag.builder()
                .id(2L)
                .group(notMatchGroup)
                .build();


        when(userRepository.findById(1L))
                .thenReturn(Optional.of(user));
        when(tagRepository.findById(2L))
                .thenReturn(Optional.of(tag));

        // when & then
        assertThrows(IllegalArgumentException.class, () -> {
            taskCRUDService.createTask("testTask",
                    LocalDateTime.MAX,
                    "personal"
                    , 2L,
                    1L,
                    1L,
                    user);
        });
    }

    @Test
    @DisplayName("createTask - user가 todoUser와 같은 그룹에 속하지 않는 경우 예외 발생")
    public void createTaskFailUserAndTodoUserDiffGroup() throws Exception {
        //given
        User todoUser = User.builder()
                .id(2L)
                .userGroup(new ArrayList<>())
                .build();

        UserGroup otherUserGroup = UserGroup.builder()
                .id(2L)
                .user(todoUser)
                .group(notMatchGroup)
                .build();



        todoUser.getUserGroup().add(otherUserGroup);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.findById(2L)).thenReturn(Optional.of(todoUser));

        // when & then
        assertThrows(IllegalArgumentException.class, () -> {
            taskCRUDService.createTask("testTask",
                    LocalDateTime.MAX,
                    "personal",
                    tagId,
                    groupId,
                    todoUser.getId(),
                    user);

        });
    }
}
