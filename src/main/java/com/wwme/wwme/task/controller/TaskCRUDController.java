package com.wwme.wwme.task.controller;

import com.wwme.wwme.group.DTO.DataWrapDTO;
import com.wwme.wwme.group.DTO.ErrorWrapDTO;
import com.wwme.wwme.login.aop.Login;
import com.wwme.wwme.task.domain.DTO.taskReceiveDTO.CreateTaskReceiveDTO;
import com.wwme.wwme.task.domain.DTO.taskReceiveDTO.TaskListReadByGroupReceiveDTO;
import com.wwme.wwme.task.domain.DTO.taskReceiveDTO.UpdateTaskReceiveDTO;
import com.wwme.wwme.task.domain.DTO.taskSendDTO.*;
import com.wwme.wwme.task.domain.Task;
import com.wwme.wwme.task.service.TaskCRUDService;
import com.wwme.wwme.user.domain.User;
import com.wwme.wwme.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.*;

@Controller
@Transactional
@RequestMapping("/task")
@Slf4j
@RequiredArgsConstructor
public class TaskCRUDController {

    private final TaskCRUDService taskCRUDService;
    private final UserService userService;


    @PostMapping
    public ResponseEntity<?> createTask(@RequestBody CreateTaskReceiveDTO createTaskReceiveDTO){
        try {
            System.out.println(createTaskReceiveDTO.getGroup_id());
            Task task = taskCRUDService.createTask(createTaskReceiveDTO);
            log.info("successfully created and added Task");
            return ResponseEntity.ok(Collections.singletonMap("task_id",task.getId()));
        } catch (Exception e) {
            log.error(e.getMessage());
            return ResponseEntity.badRequest().body(Collections.singletonMap("error",e.getMessage()));
        }
    }

    @PutMapping
    public ResponseEntity<?> updateTask(@RequestBody UpdateTaskReceiveDTO updateTaskReceiveDTO){
        try {
            Task task = taskCRUDService.updateTask(updateTaskReceiveDTO);
            UpdateTaskSendDTO updateTaskSendDTO = new UpdateTaskSendDTO();
            updateTaskSendDTO.setTask_id(task.getId());

            DataWrapDTO dataWrapDTO= new DataWrapDTO(updateTaskSendDTO);

            return ResponseEntity.ok(updateTaskSendDTO);
        } catch (Exception e) {
            log.error(e.getMessage());
            ErrorWrapDTO errorWrapDTO = new ErrorWrapDTO(e.getMessage());
            return ResponseEntity.badRequest().body(errorWrapDTO);
        }
    }

    @PostMapping("/done")
    public ResponseEntity<?> makeTaskDone(@RequestBody Long task_id,@RequestBody Boolean done){
        try{
            taskCRUDService.makeTaskDone(task_id, done);
            return ResponseEntity.ok().build();
        }catch (Exception e){
            log.error(e.getMessage());
            ErrorWrapDTO errorWrapDTO = new ErrorWrapDTO(e.getMessage());
            return ResponseEntity.badRequest().body(errorWrapDTO);
        }

    }

    @GetMapping("/list/month")
    public ResponseEntity<?> getTaskCountListforMonth(@ModelAttribute LocalDate date){
        try {
            return ResponseEntity.ok(new DataWrapDTO(taskCRUDService.getTaskCountListforMonth(date)));
        } catch (Exception e) {
            log.error(e.getMessage());
            ErrorWrapDTO errorWrapDTO = new ErrorWrapDTO(e.getMessage());
            return ResponseEntity.badRequest().body(errorWrapDTO);
        }
    }

    @GetMapping("list/day")
    public ResponseEntity<List<TaskListForDaySendDTO>> getTaskListForDay
            (@ModelAttribute LocalDate date,
           @Login User user){
        List<TaskListForDaySendDTO> taskListForDaySendDTO = taskCRUDService.getTaskListForDay(user,date);
        return ResponseEntity.ok(taskListForDaySendDTO);

    }

    @GetMapping
    public ResponseEntity<?> readOneTask(@ModelAttribute Long task_id){
        ReadOneTaskSendDTO readOneTaskSendDTO = taskCRUDService.readOneTask(task_id);

        DataWrapDTO dataWrapDTO = new DataWrapDTO(readOneTaskSendDTO);

        return ResponseEntity.ok(dataWrapDTO);
    }

    /**
     * Read all Incomplete Task of user
     *
     * @return
     */
    @GetMapping("/list/user")
    public ResponseEntity<?>
    taskListReadByUser(@Login User user,@ModelAttribute Long last_task_id){
        try {
            List<ReadTaskListByUserSendDTO> readTaskListByUserSendDTOList = taskCRUDService.getTaskListForUser(user, last_task_id);
            DataWrapDTO dataWrapDTO = new DataWrapDTO(readTaskListByUserSendDTOList);
            return ResponseEntity.ok(dataWrapDTO);
        } catch (Exception e) {
            log.error(e.getMessage());
            ErrorWrapDTO errorWrapDTO = new ErrorWrapDTO(e.getMessage());
            return ResponseEntity.badRequest().body(errorWrapDTO);
        }
    }

    @GetMapping("/list/group")
    public ResponseEntity<Map<String,List<TaskListReadByGroupSendDTO>>>
    taskListReadByGroup(@ModelAttribute TaskListReadByGroupReceiveDTO taskListReadByGroupReceiveDTO){
        List<TaskListReadByGroupSendDTO> taskListReadByGroupSendDTOList= taskCRUDService.readTaskListByGroup(taskListReadByGroupReceiveDTO);

        return ResponseEntity.ok(Collections.singletonMap("task_list",taskListReadByGroupSendDTOList));
    }

    @DeleteMapping
    public ResponseEntity<?> deleteTask(@ModelAttribute Long task_id){
        try {
            taskCRUDService.deleteTask(task_id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error(e.getMessage());
            ErrorWrapDTO errorWrapDTO = new ErrorWrapDTO(e.getMessage());
            return ResponseEntity.badRequest().body(errorWrapDTO);
        }
    }











}
