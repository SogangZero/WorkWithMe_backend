package com.wwme.wwme.task;

import com.wwme.wwme.task.domain.Task;
import com.wwme.wwme.task.repository.TaskRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;

@SpringBootTest
public class taskRepositoryTest {

    private TaskRepository taskRepository;

    @Autowired
    public taskRepositoryTest(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    @Test
    void insertTaskTest(){
        Task task = new Task();
        task.setTask_name("testTask1");
        task.setStart_time(LocalDateTime.now());
        task.setEnd_time(LocalDateTime.now().plusDays(1));
        task.setTotal_is_done(false);


        Assertions.assertThat(taskRepository.save(task)).isEqualTo(task);

    }

    @Test
    void updateTaskTest(){

        Task task = new Task();
        task.setTask_name("testTask1");
        task.setStart_time(LocalDateTime.now());
        task.setEnd_time(LocalDateTime.now().plusDays(1));
        task.setTotal_is_done(false);

        Task updtTask = taskRepository.save(task);
        updtTask.setTask_name("testUpdateTask1");

        Assertions.assertThat(taskRepository.save(updtTask).getId()).isEqualTo(updtTask.getId());
    }



    @AfterEach
    void cleanTable(){
        taskRepository.deleteAll();
    }
}
