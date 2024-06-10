package com.wwme.wwme.task.service;

import com.wwme.wwme.task.domain.DTO.receiveDTO.CreateTaskReceiveDTO;
import com.wwme.wwme.task.domain.DTO.receiveDTO.UpdateTaskReceiveDTO;
import com.wwme.wwme.task.domain.DTO.sendDTO.*;
import com.wwme.wwme.task.domain.Task;
import com.wwme.wwme.user.domain.User;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
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
     *
     * @param
     * @param user
     * @return
     */
    Collection<Task> readTaskListByGroup(Long lastId, long groupId, User user, boolean isMyTask, String completeStatus, LocalDateTime startDate, LocalDateTime endDate, boolean withDueDate, List<Long> tagList);

    /**
     * delete task identified by "task_id"
     * @param taskId
     */
    void deleteTask(Long taskId);

    boolean getIsDoneMe(User user, Task task);

    int getDoneUserCount(Task task);

    String getDoingNickname(Task task);
}
