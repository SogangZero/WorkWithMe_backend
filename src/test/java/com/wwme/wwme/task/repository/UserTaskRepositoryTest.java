package com.wwme.wwme.task.repository;

import com.wwme.wwme.task.domain.Task;
import com.wwme.wwme.task.domain.UserTask;
import com.wwme.wwme.user.domain.User;
import com.wwme.wwme.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class UserTaskRepositoryTest {

    @Autowired
    private UserTaskRepository userTaskRepository;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private UserRepository userRepository;

    private Task task;
    private User user;

    @BeforeEach
    void init() {
        user = User.builder()
                .userKey("test")
                .build();
        user = userRepository.save(user);

        task = Task.builder()
                .taskName("test")
                .build();
        task = taskRepository.save(task);

        UserTask userTask = UserTask.builder()
                .task(task)
                .user(user)
                .isDone(false)
                .build();
        userTaskRepository.save(userTask);
    }

    @Test
    @DisplayName("deleteByTask - task로 UserTask 삭제")
    @Transactional
    @Rollback
    void deleteByTaskTest() {
        //when
        userTaskRepository.deleteByTask(task);

        //then
        List<UserTask> result = userTaskRepository.findAll();
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("deleteByTaskExceptForOnePerson - 특정 유저 제외 task로 UserTask 삭제")
    @Transactional
    @Rollback
    void deleteByTaskExceptForOnePersonTest() {
        //given
        User anotherUser = User.builder()
                .userKey("anotherTest")
                .build();
        anotherUser = userRepository.save(anotherUser);

        UserTask anotherUserTask = UserTask.builder()
                .task(task)
                .user(anotherUser)
                .isDone(false)
                .build();
        userTaskRepository.save(anotherUserTask);

        //when
        userTaskRepository.deleteByTaskExceptForOnePerson(task, user);

        //then
        List<UserTask> result = userTaskRepository.findAll();
        assertThat(result.size()).isEqualTo(1);
        assertThat(result.get(0).getUser()).isEqualTo(user);
    }
}
