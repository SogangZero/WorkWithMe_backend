package com.wwme.wwme.task.service;

import com.wwme.wwme.group.domain.Group;
import com.wwme.wwme.group.repository.GroupRepository;
import com.wwme.wwme.task.domain.DTO.receiveDTO.CreateTaskReceiveDTO;
import com.wwme.wwme.task.domain.DTO.receiveDTO.TaskListReadByGroupReceiveDTO;
import com.wwme.wwme.task.domain.DTO.receiveDTO.UpdateTaskReceiveDTO;
import com.wwme.wwme.task.domain.DTO.sendDTO.*;
import com.wwme.wwme.task.domain.DTO.TaskDTO;
import com.wwme.wwme.task.domain.Tag;
import com.wwme.wwme.task.domain.Task;
import com.wwme.wwme.task.repository.TagRepository;
import com.wwme.wwme.task.repository.TaskRepository;
import com.wwme.wwme.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TaskCRUDServiceImpl implements TaskCRUDService {

    private final TaskRepository taskRepository;
    private final TagRepository tagRepository;
    private final GroupRepository groupRepository;

    @Override
    public Task createTask(CreateTaskReceiveDTO createTaskReceiveDTO) {
        return null;
    }

    @Override
    public UpdateTaskSendDTO updateTask(UpdateTaskReceiveDTO updateTaskReceiveDTO) {
        return null;
    }

    @Override
    public void makeTaskDone(Long taskId) {

    }

    @Override
    public GetTaskCountListforMonthSendDTO getTaskCountListforMonth(LocalDate date) {
        return null;
    }

    @Override
    public List<TaskListForDaySendDTO> getTaskListForDay(User user, LocalDate date) {
        return null;
    }

    @Override
    public ReadOneTaskSendDTO readOneTask(String taskId) {
        return null;
    }

    @Override
    public List<ReadTaskListByUserSendDTO> getTaskListForUser(User loginUser) {
        return null;
    }

    @Override
    public List<TaskListReadByGroupSendDTO> readTaskListByGroup(TaskListReadByGroupReceiveDTO taskListReadByGroupReceiveDTO) {
        return null;
    }

    @Override
    public void deleteTask(Long taskId) {

    }
}
