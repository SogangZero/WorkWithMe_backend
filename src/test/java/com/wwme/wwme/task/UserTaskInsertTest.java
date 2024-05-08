package com.wwme.wwme.task;


import com.wwme.wwme.task.domain.Task;
import com.wwme.wwme.task.domain.UserTask;
import com.wwme.wwme.task.repository.Tag_Repository;
import com.wwme.wwme.task.repository.Task_Repository;
import com.wwme.wwme.task.repository.UserTaskRepository;
import com.wwme.wwme.user.domain.User;
import com.wwme.wwme.user.repository.UserRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@SpringBootTest
public class UserTaskInsertTest {
    private static final Logger logger = LoggerFactory.getLogger(UserTaskInsertTest.class);

    private Tag_Repository tagRepository;
    private Task_Repository taskRepository;
    private UserRepository userRepository;
    private UserTaskRepository userTaskRepository;

    @Autowired
    public UserTaskInsertTest(Tag_Repository tagRepository, Task_Repository taskRepository, UserRepository userRepository, UserTaskRepository userTaskRepository) {
        this.tagRepository = tagRepository;
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
        this.userTaskRepository = userTaskRepository;
    }


    @Test
    @Transactional
    void testUserTaskRelationship() {
        User user = new User();
        user.setNickname("testuser");
        userRepository.save(user);

        Task task = new Task();
        task.setTask_name("Complete Project");
        task.setStart_time(LocalDateTime.now());
        task.setEnd_time(LocalDateTime.now().plusDays(1));
        taskRepository.save(task);

        UserTask userTask = new UserTask();
        userTask.setId(12L);
        userTask.setUser(user);
        userTask.setTask(task);
        userTask.setIs_done(false);
        userTaskRepository.save(userTask);


        Assertions.assertThat(userTaskRepository.findById(12L).get().getUser()).isEqualTo(user);


    }
}
