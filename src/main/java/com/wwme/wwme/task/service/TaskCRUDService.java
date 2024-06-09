package com.wwme.wwme.task.service;

import com.wwme.wwme.task.domain.DTO.taskReceiveDTO.CreateTaskReceiveDTO;
import com.wwme.wwme.task.domain.DTO.taskReceiveDTO.TaskListReadByGroupReceiveDTO;
import com.wwme.wwme.task.domain.DTO.taskReceiveDTO.UpdateTaskReceiveDTO;
import com.wwme.wwme.task.domain.DTO.taskSendDTO.*;
import com.wwme.wwme.task.domain.Task;
import com.wwme.wwme.user.domain.User;

import java.time.LocalDate;
import java.util.List;

public interface TaskCRUDService {

    /**
     *
     * @param createTaskReceiveDTO
     * @return Task entity that has been created
     */
    public Task createTask(CreateTaskReceiveDTO createTaskReceiveDTO);


    /**
     *
     * @param updateTaskReceiveDTO
     * @return updateTaskSendDTO
     */
   public  Task updateTask(UpdateTaskReceiveDTO updateTaskReceiveDTO);


    /**
     *
     * @param taskId
     * make the task of "taskid" done
     */
    public void makeTaskDone(Long taskId, Boolean done);


    /**
     *
     * @param date (year month day)
     * @return getTaskCountLIstforMonthSendDTO
     */
    public GetTaskCountListforMonthSendDTO getTaskCountListforMonth(LocalDate date);


    /**
     *
     * @param user
     * @param date
     * @return a list of tasks within the day of the "date"
     */
    public List<TaskListForDaySendDTO> getTaskListForDay(User user, LocalDate date);

    /**
     *
     * @param taskId
     * @return
     */
    public ReadOneTaskSendDTO readOneTask(Long taskId);

    /**
     * return incomlpete task of user
     *
     * @param loginUser
     * @param last_task_id
     * @return
     */
    public List<ReadTaskListByUserSendDTO> getTaskListForUser(User loginUser, Long last_task_id);


    /**
     * read task list by group
     * @param taskListReadByGroupReceiveDTO
     * @return
     */
    public List<TaskListReadByGroupSendDTO> readTaskListByGroup(TaskListReadByGroupReceiveDTO taskListReadByGroupReceiveDTO);

    /**
     * delete task identified by "task_id"
     * @param taskId
     */
    void deleteTask(Long taskId);
}
