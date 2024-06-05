package com.wwme.wwme.task.controller;

import com.wwme.wwme.login.aop.Login;
import com.wwme.wwme.task.domain.DTO.receiveDTO.CreateTaskReceiveDTO;
import com.wwme.wwme.task.domain.DTO.receiveDTO.TaskListReadByGroupReceiveDTO;
import com.wwme.wwme.task.domain.DTO.receiveDTO.UpdateTaskReceiveDTO;
import com.wwme.wwme.task.domain.DTO.sendDTO.*;
import com.wwme.wwme.task.domain.Task;
import com.wwme.wwme.task.service.TaskCRUDService;
import com.wwme.wwme.user.domain.User;
import com.wwme.wwme.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.*;

@Controller
@RequestMapping("/task")
public class TaskCRUDController {

    private final TaskCRUDService taskCRUDService;
    private final UserService userService;

    @Autowired
    public TaskCRUDController(TaskCRUDService taskCRUDService, UserService userService) {
        this.taskCRUDService = taskCRUDService;
        this.userService = userService;
    }

    @PostMapping("/")
    public ResponseEntity<Map<String,Long>> createTask(@RequestBody CreateTaskReceiveDTO createTaskReceiveDTO){
        Task task = taskCRUDService.createTask(createTaskReceiveDTO);
        return ResponseEntity.ok(Collections.singletonMap("task_id",task.getId()));
    }

    @PostMapping("/")
    public ResponseEntity<UpdateTaskSendDTO> updateTask(@RequestBody UpdateTaskReceiveDTO updateTaskReceiveDTO){
        Task task = taskCRUDService.updateTask(updateTaskReceiveDTO);

        UpdateTaskSendDTO updateTaskSendDTO = new UpdateTaskSendDTO();
        updateTaskSendDTO.setTask_id(task.getId());
        updateTaskSendDTO.setSuccess(true);

        return ResponseEntity.ok(updateTaskSendDTO);
    }

    @PostMapping("/done")
    public ResponseEntity<Map<String,Boolean>> makeTaskDone(@RequestBody Long task_id,@RequestBody Boolean done){
        try{
            taskCRUDService.makeTaskDone(task_id, done);
            return ResponseEntity.ok(Collections.singletonMap("success",true));
        }catch (Exception e){
            return ResponseEntity.badRequest().body(Collections.singletonMap("success",false));
        }

    }

    @GetMapping("/list/month")
    public ResponseEntity<GetTaskCountListforMonthSendDTO> getTaskCountListforMonth(@ModelAttribute LocalDate date){
        return ResponseEntity.ok(taskCRUDService.getTaskCountListforMonth(date));
    }

    @GetMapping("list/day")
    public ResponseEntity<List<TaskListForDaySendDTO>> getTaskListForDay
            (@ModelAttribute LocalDate date,
           @Login User user){
        List<TaskListForDaySendDTO> taskListForDaySendDTO = taskCRUDService.getTaskListForDay(user,date);
        return ResponseEntity.ok(taskListForDaySendDTO);

    }

    @GetMapping("/")
    public ResponseEntity<ReadOneTaskSendDTO> readOneTask(@ModelAttribute Long task_id){
        ReadOneTaskSendDTO readOneTaskSendDTO = taskCRUDService.readOneTask(task_id);

        return ResponseEntity.ok(readOneTaskSendDTO);
    }

    /**
     * Read all Incomplete Task of user
     *
     * @return
     */
    @GetMapping("/list/user")
    public ResponseEntity<Map<String, List<ReadTaskListByUserSendDTO>>>
    taskListReadByUser(@Login User user,@ModelAttribute Long last_task_id){
        List<ReadTaskListByUserSendDTO> readTaskListByUserSendDTOList = taskCRUDService.getTaskListForUser(user, last_task_id);
        return ResponseEntity.ok(Collections.singletonMap("task_list",readTaskListByUserSendDTOList));
    }

    @GetMapping("/list/group")
    public ResponseEntity<Map<String,List<TaskListReadByGroupSendDTO>>>
    taskListReadByGroup(@ModelAttribute TaskListReadByGroupReceiveDTO taskListReadByGroupReceiveDTO){
        List<TaskListReadByGroupSendDTO> taskListReadByGroupSendDTOList= taskCRUDService.readTaskListByGroup(taskListReadByGroupReceiveDTO);

        return ResponseEntity.ok(Collections.singletonMap("task_list",taskListReadByGroupSendDTOList));
    }

    @DeleteMapping("")
    public ResponseEntity<Map<String,Boolean>> deleteTask(@ModelAttribute Long task_id){
        taskCRUDService.deleteTask(task_id);
        return ResponseEntity.ok(Collections.singletonMap("success",true));
    }











}
