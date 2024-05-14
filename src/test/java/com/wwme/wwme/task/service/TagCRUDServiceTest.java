package com.wwme.wwme.task.service;

import com.wwme.wwme.task.domain.DTO.TagDTO;
import com.wwme.wwme.task.domain.Tag;
import com.wwme.wwme.task.domain.Task;
import com.wwme.wwme.task.repository.TagRepository;
import com.wwme.wwme.task.repository.TaskRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@SpringBootTest
@Transactional
public class TagCRUDServiceTest {
    private final TaskRepository taskRepository;
    private final TagRepository tagRepository;

    private final TagCRUDService tagCRUDService;

    Task savedTask1;
    Task savedTask2;
    Tag savedTag1;
    Tag savedTag2;

    Tag newTag1;
    Tag newTag2;


    @Autowired
    public TagCRUDServiceTest(TaskRepository taskRepository, TagRepository tagRepository, TagCRUDService tagCRUDService) {
        this.taskRepository = taskRepository;
        this.tagRepository = tagRepository;
        this.tagCRUDService = tagCRUDService;
    }

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

        newTag1 = new Tag();
        newTag1.setTag_name("newtag1");


        newTag2 = new Tag();
        newTag2.setTag_name("newtag2");


    }
    @AfterEach
    void clearDB(){
        taskRepository.deleteAll();
        tagRepository.deleteAll();
    }

    @Test
    void readOneTagTest(){
        TagDTO readTagDTO =  tagCRUDService.readOneTag(savedTag1.getId());
        Assertions.assertThat(readTagDTO.getId()).isEqualTo(savedTag1.getId());
    }
    @Test
    void createTagTest(){
        Tag createdTag = tagCRUDService.createUpdateTag(tagCRUDService.convertTagToTagDTO(newTag1));
        Assertions.assertThat(createdTag.getTag_name()).isEqualTo(newTag1.getTag_name());
    }

    @Test
    void updateTagTest(){
        TagDTO tagDTO = tagCRUDService.readOneTag(savedTag1.getId());

        tagDTO.setTag_name("updatedTag");



        Assertions.assertThat(tagCRUDService.createUpdateTag(tagDTO).getTag_name()).isEqualTo(tagDTO.getTag_name());
    }


}
