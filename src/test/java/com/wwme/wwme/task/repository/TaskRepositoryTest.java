package com.wwme.wwme.task.repository;

import com.wwme.wwme.group.domain.Group;
import com.wwme.wwme.group.repository.GroupRepository;
import com.wwme.wwme.task.domain.Tag;
import com.wwme.wwme.task.domain.Task;
import com.wwme.wwme.task.repository.TagRepository;
import com.wwme.wwme.task.repository.TaskRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@SpringBootTest
@Transactional
public class TaskRepositoryTest {

    private TaskRepository taskRepository;
    private TagRepository tagRepository;
    private GroupRepository groupRepository;

    @Autowired
    public TaskRepositoryTest(TaskRepository taskRepository, TagRepository tagRepository, GroupRepository groupRepository) {
        this.taskRepository = taskRepository;
        this.tagRepository = tagRepository;
        this.groupRepository = groupRepository;
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

    @Test
    void findTasksByGroupAndFiltersTest(){
        Task task1 = new Task();
        task1.setTask_name("testTask1");
        task1.setStart_time(LocalDateTime.now());
        task1.setEnd_time(LocalDateTime.now().plusDays(1));
        task1.getUserTaskList().clear();
        task1.setTotal_is_done(false);
        Task savedTask1 = taskRepository.save(task1);
        Assertions.assertThat(taskRepository.findById(savedTask1.getId())).isNotEmpty();


        Task task2 = new Task();
        task2.setTask_name("testTask2");
        task2.setStart_time(LocalDateTime.now());
        task2.setEnd_time(LocalDateTime.now().plusDays(1));
        task2.getUserTaskList().clear();
        task2.setTotal_is_done(false);
        Task savedTask2 = taskRepository.save(task2);
        Assertions.assertThat(savedTask2).isNotNull();



        Task task3 = new Task();
        task3.setTask_name("testTask3");
        task3.setStart_time(LocalDateTime.now());
        task3.setEnd_time(LocalDateTime.now().plusDays(1));
        task3.getUserTaskList().clear();
        task3.setTotal_is_done(false);
        Task savedTask3 = taskRepository.save(task3);
        Assertions.assertThat(savedTask3).isNotNull();

        Group group1 = new Group();
        group1.setGroupName("testGroup1");
        Group savedGroup1 = groupRepository.save(group1);
        Assertions.assertThat(savedGroup1).isNotNull();

        Group group2 = new Group();
        group1.setGroupName("testGroup2");
        Group savedGroup2 = groupRepository.save(group2);
        Assertions.assertThat(savedGroup2).isNotNull();

        savedTask1.setGroup(savedGroup1);
        savedTask1 = taskRepository.save(savedTask1);
        savedTask2.setGroup(savedGroup2);
        savedTask2 = taskRepository.save(savedTask2);

//        Assertions.assertThat(taskRepository.findById(savedTask1.getId()).get().getGroup().getId())
//                .isEqualTo(savedGroup1.getId());
        System.out.println("savedGroup1.id = " + savedGroup1.getId());
        List<Task> resultTasks1 = taskRepository.findTasksByGroupAndFilters(savedGroup1.getId(),null,null,null,null,null );

        Assertions.assertThat(resultTasks1).contains(savedTask1);
    }



    @AfterEach
    void cleanTable(){
        tagRepository.deleteAll();
        taskRepository.deleteAll();
    }
}
