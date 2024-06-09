//package com.wwme.wwme.task.service;
//
//import com.wwme.wwme.task.domain.DTO.TaskDTO;
//import com.wwme.wwme.task.domain.Task;
//import com.wwme.wwme.task.repository.TagRepository;
//import com.wwme.wwme.task.repository.TaskRepository;
//import jakarta.persistence.EntityNotFoundException;
//import org.assertj.core.api.Assertions;
//import org.junit.jupiter.api.AfterEach;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.time.LocalDateTime;
//
//@SpringBootTest
//@Transactional
//public class TaskCRUDServiceTest {
//
//    private TaskCRUDService taskCRUDService;
//    private TaskRepository taskRepository;
//    private TagRepository tagRepository;
//
//
//    private Task savedTask1;
//    private Task savedTask2;
//    private Task newTask1;
//    private Task newTask2;
//    @Autowired
//    public TaskCRUDServiceTest(TaskCRUDService taskCRUDService, TaskRepository taskRepository, TagRepository tagRepository) {
//        this.taskCRUDService = taskCRUDService;
//        this.taskRepository = taskRepository;
//        this.tagRepository = tagRepository;
//    }
//
//    @BeforeEach
//    void createTestDB(){
//        savedTask1 = new Task();
//        savedTask1.setTaskName("savedTask1");
//        savedTask1.setTaskType("personal");
//        savedTask1.setStartTime(LocalDateTime.now());
//        savedTask1.setEndTime(LocalDateTime.now().plusDays(1));
//        savedTask1.getUserTaskList().clear();
//        savedTask1.setTotalIsDone(false);
//        savedTask1 = taskRepository.save(savedTask1);
//
//        savedTask2 = new Task();
//        savedTask2.setTaskName("savedTask2");
//        savedTask2.setTaskType("personal");
//        savedTask2.setStartTime(LocalDateTime.now());
//        savedTask2.setEndTime(LocalDateTime.now().plusDays(1));
//        savedTask2.getUserTaskList().clear();
//        savedTask2.setTotalIsDone(false);
//        savedTask2 = taskRepository.save(savedTask2);
//
//        newTask1 = new Task();
//        newTask1.setTaskName("newTask1");
//        newTask1.setTaskType("personal");
//        newTask1.setStartTime(LocalDateTime.now());
//        newTask1.setEndTime(LocalDateTime.now().plusDays(1));
//        newTask1.getUserTaskList().clear();
//        newTask1.setTotalIsDone(false);
//
//        newTask2 = new Task();
//        newTask2.setTaskName("newTask2");
//        newTask2.setTaskType("personal");
//        newTask2.setStartTime(LocalDateTime.now());
//        newTask2.setEndTime(LocalDateTime.now().plusDays(1));
//        newTask2.getUserTaskList().clear();
//        newTask2.setTotalIsDone(false);
//
//        Assertions.assertThat(savedTask2).isNotNull();
//        Assertions.assertThat(savedTask1).isNotNull();
//    }
//
//    @AfterEach
//    void cleanTable(){
//        taskRepository.deleteAll();
//        tagRepository.deleteAll();
//
//    }
//    @Test
//    void readOneTaskTest(){
//        TaskDTO readTaskDTO = taskCRUDService.readOneTask(savedTask1.getId());
//        Assertions.assertThat(readTaskDTO.getId()).isEqualTo(savedTask1.getId());
//    }
//    @Test
//    void createTaskTest(){
//        TaskDTO newTaskDTO1 = taskCRUDService.convertTaskIntoTaskDTO(newTask1);
//
//        Task addedTask =  taskCRUDService.createUpdateTask(newTaskDTO1);
//
//        Assertions.assertThat(addedTask.getTaskName()).isEqualTo(newTaskDTO1.getTaskName());
//    }
//
//    @Test
//    void updateTaskTest(){
//        TaskDTO taskDTO =  taskCRUDService.readOneTask(savedTask1.getId());
//        taskDTO.setTaskName("updatedTask");
//
//        Task updated = taskCRUDService.createUpdateTask(taskDTO);
//
//        Assertions.assertThat(updated.getTaskName()).isEqualTo(taskDTO.getTaskName());
//
//    }
//
//
//
//    @Test
//    void deleteTaskTest(){
//        TaskDTO taskDTO = taskCRUDService.readOneTask(savedTask1.getId());
//
//        taskCRUDService.deleteTask(taskDTO.getId());
//
//        try{
//            taskCRUDService.readOneTask(savedTask1.getId());
//            Assertions.fail("deletion failed");
//        }catch (Exception e){
//            Assertions.assertThat(e.getClass()).isEqualTo(EntityNotFoundException.class);
//        }
//    }
//}
