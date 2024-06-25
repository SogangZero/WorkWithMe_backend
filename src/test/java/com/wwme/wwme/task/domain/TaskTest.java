package com.wwme.wwme.task.domain;

import com.wwme.wwme.group.domain.Group;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class TaskTest {
    private Task task;
    private Group group;
    private Tag tag;

    @BeforeEach
    public void init() {
        group = Group.builder()
                .id(1L)
                .build();

        tag = Tag.builder()
                .id(1L)
                .group(group)
                .build();

        task = Task.builder()
                .id(1L)
                .taskName("testTask")
                .startTime(LocalDateTime.now())
                .endTime(LocalDateTime.MAX)
                .taskType("personal")
                .totalIsDone(false)
                .tag(tag)
                .group(group)
                .userTaskList(new ArrayList<>())
                .build();
    }

    @Test
    @DisplayName("addUserTask - 인자로 들어온 userTask를 userTaskList에 추가")
    public void addUserTaskTest() throws Exception {
        //given
        UserTask userTask = UserTask.builder()
                .id(1L)
                .task(task)
                .build();

        //when
        task.addUserTask(userTask);
        //then
        assertThat(task.getUserTaskList()).contains(userTask);
    }

    @Test
    @DisplayName("changeTag - tag 변경")
    public void changeTagTest() throws Exception {
        //given
        Tag newTag = new Tag();
        //when
        task.changeTag(newTag);
        //then
        assertThat(task.getTag()).isEqualTo(newTag);
    }

    @Test
    @DisplayName("changeEndTime - 종료 시간 변경")
    void changeEndTimeTest() {
        //given
        LocalDateTime newEndTime = LocalDateTime.now().plusHours(2);

        //when
        task.changeEndTime(newEndTime);

        //then
        assertThat(task.getEndTime()).isEqualTo(newEndTime);
    }

    @Test
    @DisplayName("isBelongToGroupId - 그룹 ID 확인")
    void isBelongToGroupIdTest() {
        assertThat(task.isBelongToGroupId(1L)).isTrue();
        assertThat(task.isBelongToGroupId(2L)).isFalse();
    }

    @Test
    @DisplayName("validateTaskType - 태스크 타입 검증")
    void validateTaskTypeTest() {
        assertThat(task.validateTaskType("personal")).isTrue();
        assertThat(task.validateTaskType("group")).isTrue();
        assertThat(task.validateTaskType("anyone")).isTrue();
        assertThat(task.validateTaskType("invalid")).isFalse();
    }

    @Test
    @DisplayName("changeTaskType - 태스크 타입 변경")
    void changeTaskTypeTest() {
        //when
        task.changeTaskType("group");

        //then
        assertThat(task.getTaskType()).isEqualTo("group");

        //then
        assertThrows(IllegalArgumentException.class,
                () -> task.changeTaskType("invalid"));
    }

    @Test
    @DisplayName("validateTagIdInGroup - 태그 ID 검증")
    void validateTagIdInGroupTest() {
        //when
        assertThat(task.validateTagIdInGroup(tag)).isTrue();

        Group differentGroup = new Group();
        differentGroup.setId(2L);

        Tag differentTag = new Tag();
        differentTag.setGroup(differentGroup);

        assertThat(task.validateTagIdInGroup(differentTag)).isFalse();
    }

    @Test
    @DisplayName("isDonePersonal - 개인 태스크 완료 여부 확인")
    void isDonePersonalTest() {
        //given
        UserTask userTask = mock(UserTask.class);
        when(userTask.getIsDone()).thenReturn(true);

        task.addUserTask(userTask);
        assertThat(task.isDonePersonal()).isTrue();

        //when
        task.setTaskType("group");

        //then
        assertThat(task.isDonePersonal()).isFalse();
    }

    @Test
    @DisplayName("isDoneTotal - 전체 태스크 완료 여부 확인")
    void isDoneTotalTest() {
        //given
        UserTask userTask1 = mock(UserTask.class);
        UserTask userTask2 = mock(UserTask.class);

        when(userTask1.getIsDone()).thenReturn(true);
        when(userTask2.getIsDone()).thenReturn(true);

        task.addUserTask(userTask1);
        task.addUserTask(userTask2);

        assertThat(task.isDoneTotal()).isTrue();

        when(userTask2.getIsDone()).thenReturn(false);
        assertThat(task.isDoneTotal()).isFalse();
    }

    @Test
    @DisplayName("countDoneUser - 완료한 유저 수 확인")
    void countDoneUserTest() {
        //given
        UserTask userTask1 = mock(UserTask.class);
        UserTask userTask2 = mock(UserTask.class);

        when(userTask1.getIsDone()).thenReturn(true);
        when(userTask2.getIsDone()).thenReturn(false);

        task.addUserTask(userTask1);
        task.addUserTask(userTask2);

        assertThat(task.countDoneUser()).isEqualTo(1);
        when(userTask2.getIsDone()).thenReturn(true);
        assertThat(task.countDoneUser()).isEqualTo(2);
    }

}
