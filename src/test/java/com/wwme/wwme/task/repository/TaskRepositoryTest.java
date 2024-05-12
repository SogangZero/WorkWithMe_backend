package com.wwme.wwme.task.repository;

import com.wwme.wwme.task.domain.Tag;
import com.wwme.wwme.task.domain.Task;
import com.wwme.wwme.task.repository.TagRepository;
import com.wwme.wwme.task.repository.TaskRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.Optional;

@SpringBootTest
public class TaskRepositoryTest {

    private TaskRepository taskRepository;
    private TagRepository tagRepository;

    @Autowired
    public TaskRepositoryTest(TaskRepository taskRepository, TagRepository tagRepository) {
        this.taskRepository = taskRepository;
        this.tagRepository = tagRepository;
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

    @Test
    void addTagToTaskTest(){
        Task task = new Task();
        task.setTask_name("testTask1");
        task.setStart_time(LocalDateTime.now());
        task.setEnd_time(LocalDateTime.now().plusDays(1));
        task.getUserTaskList().clear();
        task.setTotal_is_done(false);

        Tag tag = new Tag();
        tag.setTag_name("testTag1");
        tag.getTask_list().clear();
        Tag saveTag = tagRepository.save(tag);


        Task updtTask = taskRepository.save(task);
        updtTask.setTag(tag);
        taskRepository.save(updtTask);

        Assertions.assertThat(taskRepository.save(updtTask).getTag().getId()).isEqualTo(saveTag.getId());
    }

    @Test
    void deleteTaskTest(){
        Task task = new Task();
        task.setId(1L);
        task.setTask_name("testTask1");
        task.setStart_time(LocalDateTime.now());
        task.setEnd_time(LocalDateTime.now().plusDays(1));
        task.getUserTaskList().clear();
        task.setTotal_is_done(false);

        Task saveTask = taskRepository.save(task);

        taskRepository.delete(saveTask);

        Assertions.assertThat(taskRepository.findById(1L)).isEqualTo(Optional.empty());
    }



    @AfterEach
    void cleanTable(){
        tagRepository.deleteAll();
        taskRepository.deleteAll();
    }
}
