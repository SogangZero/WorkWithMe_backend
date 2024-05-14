package com.wwme.wwme.task.repository;

import com.wwme.wwme.task.domain.Tag;
import com.wwme.wwme.task.domain.Task;
import com.wwme.wwme.task.repository.TagRepository;
import com.wwme.wwme.task.repository.TaskRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@SpringBootTest
@Transactional
public class TagRepositoryTest {
    private final TaskRepository taskRepository;
    private final TagRepository tagRepository;

    Task savedTask1;
    Task savedTask2;

    Tag savedTag1;
    Tag savedTag2;

    @Autowired
    public TagRepositoryTest(TaskRepository taskRepository, TagRepository tagRepository) {
        this.taskRepository = taskRepository;
        this.tagRepository = tagRepository;
    }

    //TODO: involve group repository
    @BeforeEach
    void setDBAndInsertTagTask(){
        tagRepository.deleteAll();
        taskRepository.deleteAll();

        Tag tag1 = new Tag();
        tag1.setTag_name("testtag1");
        savedTag1 = tagRepository.save(tag1);

        Tag tag2 = new Tag();
        tag2.setTag_name("testtag1");
        savedTag2 = tagRepository.save(tag2);

        Task task1 = new Task();
        task1.setTask_name("testTask1");
        task1.setStart_time(LocalDateTime.now());
        task1.setEnd_time(LocalDateTime.now().plusDays(1));
        task1.setTotal_is_done(false);
        savedTask1 = taskRepository.save(task1);

        Task task2 = new Task();
        task2.setTask_name("testTask2");
        task2.setStart_time(LocalDateTime.now());
        task2.setEnd_time(LocalDateTime.now().plusDays(1));
        task2.setTotal_is_done(false);
        savedTask2 = taskRepository.save(task2);

        Assertions.assertThat(taskRepository.findById(savedTask1.getId())).isPresent();
        Assertions.assertThat(taskRepository.findById(savedTask2.getId())).isPresent();
        Assertions.assertThat(tagRepository.findById(savedTag1.getId())).isPresent();
        Assertions.assertThat(tagRepository.findById(savedTag2.getId())).isPresent();

    }

    @Test
    void addTaskToTaskListOfTag(){
        savedTag1.addToTaskList(savedTask1);
        savedTag1.addToTaskList(savedTask2);
        tagRepository.save(savedTag1);

        Tag taskAddedTag =  tagRepository.findById(savedTag1.getId()).orElseThrow(()-> new EntityNotFoundException("Task with the Tag added not found in DB"));

        Assertions.assertThat(taskAddedTag.getTask_list()).contains(savedTask1,savedTask2);
    }

    /**
     * Deletion and updating of the task_list inside tag will be tested in the service layer, since
     * it requires the use of the Entity Manager
     */

    @Test
    void deleteTag(){
        tagRepository.deleteById(savedTag1.getId());
        Assertions.assertThat(tagRepository.findById(savedTag1.getId())).isEmpty();
    }
}
