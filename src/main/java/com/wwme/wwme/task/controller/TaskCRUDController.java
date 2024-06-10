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
import com.wwme.wwme.user.domain.User;
import com.wwme.wwme.user.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Controller
@Transactional
@Slf4j
@RequestMapping("/task")
public class TaskCRUDController {

    private final TaskCRUDService taskCRUDService;
    private final UserService userService;
    private static final Logger logger = LoggerFactory.getLogger(TaskCRUDController.class);

    @Autowired
    public TaskCRUDController(TaskCRUDService taskCRUDService, UserService userService) {
        this.taskCRUDService = taskCRUDService;
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<?> createTask(@RequestBody CreateTaskReceiveDTO createTaskReceiveDTO) {
        try {
            System.out.println(createTaskReceiveDTO.getGroup_id());
            Task task = taskCRUDService.createTask(createTaskReceiveDTO);
            logger.info("successfully created and added Task");
            return ResponseEntity.ok(Collections.singletonMap("task_id", task.getId()));
        } catch (Exception e) {
            logger.error(e.getMessage());
            return ResponseEntity.badRequest().body(Collections.singletonMap("error", e.getMessage()));
        }
    }

    @PutMapping
    public ResponseEntity<?> updateTask(@RequestBody UpdateTaskReceiveDTO updateTaskReceiveDTO) {
        try {
            Task task = taskCRUDService.updateTask(updateTaskReceiveDTO);
            UpdateTaskSendDTO updateTaskSendDTO = new UpdateTaskSendDTO();
            updateTaskSendDTO.setTask_id(task.getId());

            DataWrapDTO dataWrapDTO = new DataWrapDTO(updateTaskSendDTO);

            return ResponseEntity.ok(updateTaskSendDTO);
        } catch (Exception e) {
            logger.error(e.getMessage());
            ErrorWrapDTO errorWrapDTO = new ErrorWrapDTO(e.getMessage());
            return ResponseEntity.badRequest().body(errorWrapDTO);
        }
    }

    @PostMapping("/done")
    public ResponseEntity<?> makeTaskDone(@RequestBody Long task_id, @RequestBody Boolean done) {
        try {
            taskCRUDService.makeTaskDone(task_id, done);
            return ResponseEntity.ok(null); //TODO: 데이터가 없을떄 명세서에 의하면 아무것도 보내지 않는다. Is this ok?
        } catch (Exception e) {
            logger.error(e.getMessage());
            ErrorWrapDTO errorWrapDTO = new ErrorWrapDTO(e.getMessage());
            return ResponseEntity.badRequest().body(errorWrapDTO);
        }

    }

    @GetMapping("/list/month")
    public ResponseEntity<?> getTaskCountListforMonth(@ModelAttribute LocalDate date) {
        try {
            return ResponseEntity.ok(new DataWrapDTO(taskCRUDService.getTaskCountListforMonth(date)));
        } catch (Exception e) {
            logger.error(e.getMessage());
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
            logger.error(e.getMessage());
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
            return ResponseEntity.ok(null); //TODO: 데이터가 없을떄 명세서에 의하면 아무것도 보내지 않는다. Is this ok?
        } catch (Exception e) {
            logger.error(e.getMessage());
            ErrorWrapDTO errorWrapDTO = new ErrorWrapDTO(e.getMessage());
            return ResponseEntity.badRequest().body(errorWrapDTO);
        }
    }


}
