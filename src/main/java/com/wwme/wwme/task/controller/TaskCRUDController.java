package com.wwme.wwme.task.controller;

import com.wwme.wwme.task.domain.DTO.TaskDTO;
import com.wwme.wwme.task.service.TaskCRUDService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
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

    @PostMapping("/group")
    public ResponseEntity<Map<String, List<TaskDTO>>> getTaskListByGroup(@RequestBody TaskDTO taskDTO){
        return null;
    }

    @PostMapping("/user")
    public ResponseEntity<Map<String, List<TaskDTO>>> getTaskListByUser(@RequestBody TaskDTO taskDTO){
        return null;
    }

    @DeleteMapping("/")
    public ResponseEntity<Object> deleteTask(@RequestBody Long task_id){
        taskCRUDService.deleteTask(task_id);
        return ResponseEntity.ok(Collections.singletonMap("success",true));
    }





}
