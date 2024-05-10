package com.wwme.wwme.task;


import com.wwme.wwme.task.domain.Task;
import com.wwme.wwme.task.domain.UserTask;
import com.wwme.wwme.task.repository.TagRepository;
import com.wwme.wwme.task.repository.TaskRepository;
import com.wwme.wwme.task.repository.UserTaskRepository;
import com.wwme.wwme.user.domain.User;
import com.wwme.wwme.user.repository.UserRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;

@SpringBootTest
public class UserTaskRepositoryTest {
    private static final Logger logger = LoggerFactory.getLogger(UserTaskRepositoryTest.class);
    private TagRepository tagRepository;
    private TaskRepository taskRepository;
    private UserRepository userRepository;
    private UserTaskRepository userTaskRepository;


    private User savedUser;
    private Task savedTask;

    @Autowired
    public UserTaskRepositoryTest(TagRepository tagRepository, TaskRepository taskRepository, UserRepository userRepository, UserTaskRepository userTaskRepository) {
        this.tagRepository = tagRepository;
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
        this.userTaskRepository = userTaskRepository;
    }


    @BeforeEach
    void insertUserAndTest(){
        userRepository.deleteAll();
        taskRepository.deleteAll();

        userRepository.deleteAll();
        //insert Test task
        Task task = new Task();
        task.setTask_name("testTask1");
        task.setStart_time(LocalDateTime.now());
        task.setEnd_time(LocalDateTime.now().plusDays(1));
        task.setTotal_is_done(false);
        savedTask = taskRepository.save(task);

        //insert Test User
        User user = new User();
        user.setRegister_date(LocalDateTime.now());
        user.setNickname("testUser1");
        user.setSocial_provider("seswses?");
        savedUser = userRepository.save(user);
    }

    @Test
    void testUserTaskRelationship() {

        Assertions.assertThat(savedUser).isNotNull();
        Assertions.assertThat(savedTask).isNotNull();

        UserTask userTask = new UserTask();
        userTask.setUser(savedUser);
        userTask.setTask(savedTask);
        userTask.setIs_done(false);

        UserTask savedUserTask = userTaskRepository.save(userTask);
        Assertions.assertThat(savedUserTask.getUser().getId()).isEqualTo(savedUser.getId());
        Assertions.assertThat(savedUserTask.getTask().getId()).isEqualTo(savedTask.getId());

    }
}
