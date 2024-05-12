package com.wwme.wwme.task.controller;

import com.wwme.wwme.task.domain.DTO.TaskDTO;
import com.wwme.wwme.task.domain.Task;
import com.wwme.wwme.task.service.TaskCRUDService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/task")
public class TaskCRUDController {

    private TaskCRUDService taskCRUDService;

    @Autowired
    public TaskCRUDController(TaskCRUDService taskCRUDService) {
        this.taskCRUDService = taskCRUDService;
    }


    @PostMapping("/")
    public ResponseEntity<Object> createTask(@RequestBody TaskDTO taskDTO){
        taskCRUDService.createUpdateTask(taskDTO);
        return ResponseEntity.ok(Collections.singletonMap("success",true));
    }

    @PutMapping("/")
    public ResponseEntity<Object> updateTask(@RequestBody TaskDTO taskDTO){
        taskCRUDService.createUpdateTask(taskDTO);
        return ResponseEntity.ok(Collections.singletonMap("success",true));
    }

    @GetMapping("/")
    public ResponseEntity<TaskDTO> readOneTask(@RequestBody Long task_id){
        TaskDTO taskDTO = taskCRUDService.readOneTask(task_id);
        return ResponseEntity.ok(taskDTO);
    }

    @GetMapping("/group")
    public ResponseEntity<Map<String, List<TaskDTO>>> getTaskListByGroup(@RequestBody TaskDTO taskDTO){
        List<TaskDTO> taskList =  taskCRUDService.readTaskListByGroup(taskDTO);
        Map<String,List<TaskDTO>> listMap = new HashMap<>();
        listMap.put("task_list",taskList);
        return ResponseEntity.ok(listMap);
    }

    @GetMapping("/user")
    public ResponseEntity<Map<String, List<TaskDTO>>> getTaskListByUser(@RequestBody TaskDTO taskDTO){
        List<TaskDTO> taskList =  taskCRUDService.readTaskListByUser(taskDTO);
        Map<String,List<TaskDTO>> listMap = new HashMap<>();
        listMap.put("task_list",taskList);
        return ResponseEntity.ok(listMap);
    }

    @DeleteMapping("/")
    public ResponseEntity<Object> deleteTask(@RequestBody Long task_id){
        taskCRUDService.deleteTask(task_id);
        return ResponseEntity.ok(Collections.singletonMap("success",true));
    }





}
