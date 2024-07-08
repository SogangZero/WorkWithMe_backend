package com.wwme.wwme.task.controller;

import com.wwme.wwme.group.DTO.DataWrapDTO;
import com.wwme.wwme.group.DTO.ErrorWrapDTO;
import com.wwme.wwme.login.aop.Login;
import com.wwme.wwme.task.domain.DTO.receiveDTO.CreateTaskReceiveDTO;
import com.wwme.wwme.task.domain.DTO.receiveDTO.MakeTaskDoneReceiveDTO;
import com.wwme.wwme.task.domain.DTO.receiveDTO.UpdateTaskReceiveDTO;
import com.wwme.wwme.task.domain.DTO.sendDTO.*;
import com.wwme.wwme.task.domain.Task;
import com.wwme.wwme.task.service.TaskCRUDService;
import com.wwme.wwme.task.service.TaskDTOBinder;
import com.wwme.wwme.user.domain.User;
import com.wwme.wwme.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import lombok.extern.slf4j.Slf4j;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Controller
@Slf4j
@RequestMapping("/task")
@RequiredArgsConstructor
public class TaskCRUDController {

    private final TaskCRUDService taskCRUDService;
    private final TaskDTOBinder taskDTOBinder;
    private final UserService userService;


    @PostMapping
    public ResponseEntity<?> createTask(@RequestBody CreateTaskReceiveDTO createTaskReceiveDTO,
                                        @Login User user) {
        log.info("Create Task Controller");
        log.info("Test Log createTask");
        try {
            log.info("Inside Try block of createTask");
            Task task = taskCRUDService.createTask(createTaskReceiveDTO.getTask_name(),
                    createTaskReceiveDTO.getEnd_time(),
                    createTaskReceiveDTO.getTask_type(),
                    createTaskReceiveDTO.getTag_id(),
                    createTaskReceiveDTO.getGroup_id(),
                    createTaskReceiveDTO.getTodo_user_id(),
                    user);

            log.info("Create New Task[{}]", task.getId());

            CUTaskSendDTO cuTaskSendDTO = taskDTOBinder.bindCUTaskSendDTO(task,user);
            return new ResponseEntity<>(new DataResponseDTO(cuTaskSendDTO), HttpStatus.OK);   
        } catch (Exception e) {
            log.error("Create New Task ERROR " + e.getMessage());
            return new ResponseEntity<>(new ErrorResponseDTO(e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping
    public ResponseEntity<?> updateTask(@RequestBody UpdateTaskReceiveDTO updateTaskReceiveDTO,
                                        @Login User loginUser) {
        log.info("Update Task Controller");
        try {
            Task task = taskCRUDService.updateTask(updateTaskReceiveDTO.getTask_id(),
                    updateTaskReceiveDTO.getTask_name(),
                    updateTaskReceiveDTO.getEnd_time(),
                    updateTaskReceiveDTO.getTask_type(),
                    updateTaskReceiveDTO.getTag_id(),
                    updateTaskReceiveDTO.getTodo_user_id(),
                    loginUser);

            CUTaskSendDTO cuTaskSendDTO = taskDTOBinder.bindCUTaskSendDTO(task,loginUser);
            return new ResponseEntity<>(new DataResponseDTO(cuTaskSendDTO), HttpStatus.OK);
        } catch (Exception e) {
            log.error("Update Task ERROR " + e.getMessage() + Arrays.toString(e.getStackTrace()));
            return new ResponseEntity<>(new ErrorResponseDTO(e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/done")
    public ResponseEntity<?> makeTaskDone(@RequestBody MakeTaskDoneReceiveDTO makeTaskDoneReceiveDTO,
                                          @Login User user)     {
        try {
            MakeTaskDoneSendDTO makeTaskDoneSendDTO = taskCRUDService.makeTaskDone(makeTaskDoneReceiveDTO,user);
//            DataWrapDTO dataWrapDTO = new DataWrapDTO(makeTaskDoneSendDTO); //No more returns (0624)
            return ResponseEntity.ok().build();
        }catch (Exception e){
            log.error(e.getMessage());
            ErrorWrapDTO errorWrapDTO = new ErrorWrapDTO(e.getMessage());
            return ResponseEntity.badRequest().body(errorWrapDTO);
        }

    }

    @GetMapping("/list/month")
    public ResponseEntity<?> getTaskCountListforMonth(@ModelAttribute("date") LocalDate date) throws Exception{
        try {
            return ResponseEntity.ok(new DataWrapDTO(taskCRUDService.getTaskCountListforMonth(date)));
        } catch (Exception e) {
            log.error(e.getMessage());
            ErrorWrapDTO errorWrapDTO = new ErrorWrapDTO(e.getMessage());
            return ResponseEntity.badRequest().body(errorWrapDTO);
        }
    }

    @GetMapping("list/day")
    public ResponseEntity<?> getTaskListForDay
            (@ModelAttribute("date") LocalDate date,
             @Login User user) {
        try {
            List<TaskListForDaySendDTO> taskListForDaySendDTO = taskCRUDService.getTaskListForDay(user, date);
            return ResponseEntity.ok(taskListForDaySendDTO);
        } catch (Exception e) {
            log.error(e.getMessage());
            ErrorWrapDTO errorWrapDTO = new ErrorWrapDTO(e.getMessage());
            return ResponseEntity.badRequest().body(errorWrapDTO);
        }

    }

    @GetMapping
    public ResponseEntity<?> readOneTask(@ModelAttribute("task_id") Long task_id,
                                         @Login User user) {
        try {
            ReadOneTaskSendDTO readOneTaskSendDTO = taskCRUDService.readOneTask(task_id,user);
            DataWrapDTO dataWrapDTO = new DataWrapDTO(readOneTaskSendDTO);
            return ResponseEntity.ok(dataWrapDTO);
        } catch (Exception e) {
            log.error(e.getMessage());
            ErrorWrapDTO errorWrapDTO = new ErrorWrapDTO(e.getMessage());
            return ResponseEntity.badRequest().body(errorWrapDTO);
        }
    }


    @GetMapping("/list/user")
    public ResponseEntity<?>
    taskListReadByUser(@Login User user,
                       @RequestParam(name = "last_task_id", required = false) Long last_task_id) {
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
    public ResponseEntity<?> taskListReadByGroup(
            @RequestParam(name = "last_id", required = false) Long lastId,
            @RequestParam("group_id") long groupId,
            @RequestParam("is_my_task") boolean isMyTask,
            @RequestParam("complete_status") String completeStatus,
            @RequestParam(value = "start_date", required = false) LocalDateTime startDate,
            @RequestParam(value = "end_date", required = false) LocalDateTime endDate,
            @RequestParam("with_due_date") boolean withDueDate,
            @RequestParam("tag_list") List<Long> tagList,
            @Login User user
    ) {
        try {
            log.info("input: last_id:{} group_id{} isMyTask:{} completeStatus:{} startDate:{} endDate:{} withDueDate:{} tagList:{} ",
                    lastId, groupId, isMyTask, completeStatus, startDate, endDate, withDueDate, tagList);
            var taskList =
                    taskCRUDService.readTaskListByGroup(
                            lastId,
                            groupId,
                            user,
                            isMyTask,
                            completeStatus,
                            startDate,
                            endDate,
                            withDueDate,
                            tagList
                    );

            var taskDTOList = taskList.stream().map((task) -> {
                        var tag = task.getTag();
                        Long tagId = null;
                        if (tag != null)
                            tagId = task.getId();
                        return new TaskListReadByGroupSendDTO.Task(
                                task.getId(),
                                task.getTaskName(),
                                task.getEndTime(),
                                task.getTaskType(),
                                tagId,
                                task.getTotalIsDone(),
                                taskCRUDService.getIsDoneMe(user, task),
                                taskCRUDService.getDoneUserCount(task),
                                taskCRUDService.getDoingNickname(task)
                        );
                    }

            ).toList();
            var responseDTO = new TaskListReadByGroupSendDTO(taskDTOList);
            return ResponseEntity
                    .ok()
                    .body(responseDTO);
        } catch (Exception e) {
            log.error("input: last_id:{} group_id{} isMyTask:{} completeStatus:{} startDate:{} endDate:{} withDueDate:{} tagList:{} ",
                    lastId, groupId, isMyTask, completeStatus, startDate, endDate, withDueDate, tagList, e);
            return ResponseEntity
                    .badRequest()
                    .body(new ErrorWrapDTO(e.getMessage()));
        }
    }

    @DeleteMapping
    public ResponseEntity<?> deleteTask(@RequestParam("task_id") Long task_id) {
        try {
            log.info("Task Id : "+task_id);
            taskCRUDService.deleteTask(task_id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error(e.getMessage());
            ErrorWrapDTO errorWrapDTO = new ErrorWrapDTO(e.getMessage());
            return ResponseEntity.badRequest().body(errorWrapDTO);
        }
    }


}
