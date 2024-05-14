package com.wwme.wwme.task.repository;


import com.wwme.wwme.task.domain.Task;
import com.wwme.wwme.task.domain.UserTask;
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


    private User savedUser1;
    private Task savedTask1;
    private User savedUser2;
    private Task savedTask2;


    @Autowired
    public UserTaskRepositoryTest(TagRepository tagRepository, TaskRepository taskRepository, UserRepository userRepository, UserTaskRepository userTaskRepository) {
        this.tagRepository = tagRepository;
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
        this.userTaskRepository = userTaskRepository;
    }


    @BeforeEach
    void insertUserAndTask(){
        userRepository.deleteAll();
        taskRepository.deleteAll();

        //insert Test task
        Task task = new Task();
        task.setTask_name("testTask1");
        task.setStart_time(LocalDateTime.now());
        task.setEnd_time(LocalDateTime.now().plusDays(1));
        task.setTotal_is_done(false);
        savedTask1 = taskRepository.save(task);

        Task task2 = new Task();
        task2.setTask_name("testTask2");
        task2.setStart_time(LocalDateTime.now());
        task2.setEnd_time(LocalDateTime.now().plusDays(1));
        task2.setTotal_is_done(false);
        savedTask2 = taskRepository.save(task2);

        //insert Test User
        User user = new User();
        user.setRegister_date(LocalDateTime.now());
        user.setNickname("testUser1");
        user.setSocial_provider("seswses?");
        savedUser1 = userRepository.save(user);

        User user2 = new User();
        user2.setRegister_date(LocalDateTime.now());
        user2.setNickname("testUser2");
        user2.setSocial_provider("what?");
        savedUser2 = userRepository.save(user2);
    }

    @Test
    void insertUserTask() {

        Assertions.assertThat(savedUser1).isNotNull();
        Assertions.assertThat(savedTask1).isNotNull();

        UserTask userTask = new UserTask();
        userTask.setUser(savedUser1);
        userTask.setTask(savedTask1);
        userTask.setIs_done(false);

        UserTask savedUserTask = userTaskRepository.save(userTask);
        Assertions.assertThat(savedUserTask.getUser().getId()).isEqualTo(savedUser1.getId());
        Assertions.assertThat(savedUserTask.getTask().getId()).isEqualTo(savedTask1.getId());

    }

    @Test
    void cascadeUserTaskTest(){
        Assertions.assertThat(savedUser1).isNotNull();
        Assertions.assertThat(savedTask1).isNotNull();

        UserTask userTask = new UserTask();
        userTask.setUser(savedUser1);
        userTask.setTask(savedTask1);
        userTask.setIs_done(false);
        UserTask savedUserTask = userTaskRepository.save(userTask);


        userRepository.deleteById(savedUser1.getId());

        Assertions.assertThat(userTaskRepository.findById(savedUserTask.getId())).isEmpty();
    }

}
