package com.wwme.wwme.task.controller;

import com.wwme.wwme.group.DTO.DataWrapDTO;
import com.wwme.wwme.group.DTO.ErrorWrapDTO;
import com.wwme.wwme.login.aop.Login;
import com.wwme.wwme.task.domain.DTO.receiveDTO.CreateTaskReceiveDTO;
import com.wwme.wwme.task.domain.DTO.receiveDTO.TaskListReadByGroupReceiveDTO;
import com.wwme.wwme.task.domain.DTO.receiveDTO.UpdateTaskReceiveDTO;
import com.wwme.wwme.task.domain.DTO.sendDTO.*;
import com.wwme.wwme.task.domain.Task;
import com.wwme.wwme.task.service.TaskCRUDService;
import com.wwme.wwme.task.service.TaskDTOBinder;
import com.wwme.wwme.user.domain.User;
import com.wwme.wwme.user.service.UserService;
import lombok.RequiredArgsConstructor;
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
        try {
            Task task = taskCRUDService.createTask(createTaskReceiveDTO.getTask_name(),
                    createTaskReceiveDTO.getEnd_time(),
                    createTaskReceiveDTO.getTask_type(),
                    createTaskReceiveDTO.getTag_id(),
                    createTaskReceiveDTO.getGroup_id(),
                    createTaskReceiveDTO.getTodo_user_id(),
                    user);

            log.info("Create New Task[{}]", task.getId());

            CUTaskSendDTO cuTaskSendDTO = taskDTOBinder.bindCUTaskSendDTO(task);
            return new ResponseEntity<>(new DataResponseDTO(cuTaskSendDTO), HttpStatus.OK);
        } catch (Exception e) {
            log.error("Create New Task ERROR " + e.getMessage());
            return new ResponseEntity<>(new ErrorResponseDTO(e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping
    public ResponseEntity<?> updateTask(@RequestBody UpdateTaskReceiveDTO updateTaskReceiveDTO,
                                        @Login User user) {
        log.info("Update Task Controller");
        try {
            Task task = taskCRUDService.updateTask(updateTaskReceiveDTO.getTask_id(),
                    updateTaskReceiveDTO.getEnd_time(),
                    updateTaskReceiveDTO.getTask_type(),
                    updateTaskReceiveDTO.getTag_id(),
                    updateTaskReceiveDTO.getTodo_user_id(),
                    user);

            CUTaskSendDTO cuTaskSendDTO = taskDTOBinder.bindCUTaskSendDTO(task);
            return new ResponseEntity<>(new DataResponseDTO(cuTaskSendDTO), HttpStatus.OK);
        } catch (Exception e) {
            log.error("Update Task ERROR " + e.getMessage());
            return new ResponseEntity<>(new ErrorResponseDTO(e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/done")
    public ResponseEntity<?> makeTaskDone(@RequestBody Long task_id, @RequestBody Boolean done) {
        try {
            taskCRUDService.makeTaskDone(task_id, done);
            return ResponseEntity.ok().build();
        }catch (Exception e){
            log.error(e.getMessage());
            ErrorWrapDTO errorWrapDTO = new ErrorWrapDTO(e.getMessage());
            return ResponseEntity.badRequest().body(errorWrapDTO);
        }

    }

    @GetMapping("/list/month")
    public ResponseEntity<?> getTaskCountListforMonth(@ModelAttribute LocalDate date) {
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
             @Login User user) {
        List<TaskListForDaySendDTO> taskListForDaySendDTO = taskCRUDService.getTaskListForDay(user, date);
        return ResponseEntity.ok(taskListForDaySendDTO);

    }

    @GetMapping
    public ResponseEntity<?> readOneTask(@ModelAttribute Long task_id) {
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
    taskListReadByUser(@Login User user, @ModelAttribute Long last_task_id) {
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
            @RequestParam("start_date") LocalDateTime startDate,
            @RequestParam("end_date") LocalDateTime endDate,
            @RequestParam("with_due_date") boolean withDueDate,
            @RequestParam("tag_list") List<Long> tagList,
            @Login User user
    ) {
        try {
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

            var taskDTOList = taskList.stream().map((task) ->
                new TaskListReadByGroupSendDTO.Task(
                        task.getId(),
                        task.getTaskName(),
                        task.getEndTime(),
                        task.getTaskType(),
                        task.getTag().getId(),
                        task.getTotalIsDone(),
                        taskCRUDService.getIsDoneMe(user, task),
                        taskCRUDService.getDoneUserCount(task),
                        taskCRUDService.getDoingNickname(task)
                )

            ).toList();
            var responseDTO = new TaskListReadByGroupSendDTO(taskDTOList);
            return ResponseEntity
                    .ok()
                    .body(new DataWrapDTO(responseDTO));
        } catch (Exception e) {
            log.error("input: last_id:{} group_id{} isMyTask:{} completeStatus:{} startDate:{} endDate:{} withDueDate:{} tagList:{} ",
                    lastId, groupId, isMyTask, completeStatus, startDate, endDate, withDueDate, tagList, e);
            return ResponseEntity
                    .badRequest()
                    .body(new ErrorWrapDTO(e.getMessage()));
        }
    }

    @DeleteMapping
    public ResponseEntity<?> deleteTask(@ModelAttribute Long task_id) {
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
