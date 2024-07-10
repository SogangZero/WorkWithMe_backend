package com.wwme.wwme.task.service;

import com.wwme.wwme.task.domain.DTO.receiveDTO.CreateTaskReceiveDTO;
import com.wwme.wwme.task.domain.DTO.receiveDTO.MakeTaskDoneReceiveDTO;
import com.wwme.wwme.task.domain.DTO.receiveDTO.UpdateTaskReceiveDTO;
import com.wwme.wwme.task.domain.DTO.sendDTO.*;
import com.wwme.wwme.task.domain.Task;
import com.wwme.wwme.task.domain.UserTask;
import com.wwme.wwme.user.domain.User;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

public interface TaskCRUDService {

 /**
  * @param
  * @return Task entity that has been created
  */
 public Task createTask(String taskName,
                        LocalDateTime endTime,
                        String taskType,
                        Long tagId,
                        Long groupId,
                        Long todoUserId,
                        User user);


 /**
  * @return updateTaskSendDTO
  */
 public Task updateTask(Long taskId,
                        LocalDateTime endTime,
                        String taskType,
                        Long tagId,
                        Long todoUserId,
                        User user);


    /**
     * @param makeTaskDoneReceiveDTO
     * @param user
     * @return
     */
    public MakeTaskDoneSendDTO makeTaskDone(MakeTaskDoneReceiveDTO makeTaskDoneReceiveDTO, User user) throws Exception;


    /**
     *
     * @param date (year month day)
     * @return getTaskCountLIstforMonthSendDTO
     */
    public GetTaskCountListforMonthSendDTO getTaskCountListforMonth(LocalDate date) throws Exception;


    /**
     *
     * @param user
     * @param date
     * @return a list of tasks within the day of the "date"
     */
    public List<TaskListForDaySendDTO> getTaskListForDay(User user, LocalDate date) throws Exception;

    /**
     *
     * @param taskId
     * @return
     */
    public ReadOneTaskSendDTO readOneTask(Long taskId, User loginUser);

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

    Collection<UserTask> findAllTodayDueDateTasks();
}
