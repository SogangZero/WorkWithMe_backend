package com.wwme.wwme.task.service;

import com.wwme.wwme.group.domain.Group;
import com.wwme.wwme.group.repository.GroupRepository;
import com.wwme.wwme.task.domain.DTO.TaskDTO;
import com.wwme.wwme.task.domain.Tag;
import com.wwme.wwme.task.domain.Task;
import com.wwme.wwme.task.repository.TagRepository;
import com.wwme.wwme.task.repository.TaskRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TaskCRUDService {

    private final TaskRepository taskRepository;
    private final TagRepository tagRepository;
    private final GroupRepository groupRepository;



    public Task createUpdateTask(TaskDTO taskDTO){
        Task task = convertTaskDTOIntoTask(taskDTO);
        return taskRepository.save(task);
    }

    public TaskDTO readOneTask(Long task_id){
        Task task = taskRepository.findById(task_id).orElseThrow(()-> new EntityNotFoundException("Task from Task ID not found"));

        return convertTaskIntoTaskDTO(task);
    }

    public List<TaskDTO> readTaskListByGroup(TaskDTO taskDTO){
        List<Task> task = taskRepository.findTasksByGroupAndFilters(
                taskDTO.getGroupId(),taskDTO.getUserId(),taskDTO.getTagId(),taskDTO.getStartTime(),
                taskDTO.getEndTime(),taskDTO.getIsDone());

        List<TaskDTO> taskDTOS = new ArrayList<>();
        for(Task t : task){
            if(t!=null){
                taskDTOS.add(convertTaskIntoTaskDTO(t));
            }
        }
        return taskDTOS;
    }

    public List<TaskDTO> readTaskListByUser(TaskDTO taskDTO){
        List<Task> task = taskRepository.findTaskByUserIdAndFilters(
                taskDTO.getUserId(),taskDTO.getTagId(),taskDTO.getStartTime(),
                taskDTO.getEndTime(),taskDTO.getIsDone());

        List<TaskDTO> taskDTOS = new ArrayList<>();
        for(Task t : task){
            if(t!=null){
                taskDTOS.add(convertTaskIntoTaskDTO(t));
            }
        }
        return taskDTOS;
    }


    public void deleteTask(Long taskId){
        taskRepository.deleteById(taskId);

        if(taskRepository.findById(taskId).isPresent()){
            throw new RuntimeException("Task delete error : deletion failed");
        }

    }







    // Friend Functions

    public Task convertTaskDTOIntoTask(TaskDTO taskDTO){
        Task task = new Task();

        if(taskDTO.getId() != null){
            task.setId(taskDTO.getId());
        }
        task.setTaskName(taskDTO.getTaskName());
        task.setStartTime(taskDTO.getStartTime());
        task.setEndTime(taskDTO.getEndTime());
        task.setTaskType(taskDTO.getTaskType());
        task.setTotalIsDone(taskDTO.getIsDone());

        if(taskDTO.getTagId() != null){
            Tag tag = tagRepository.findById(taskDTO.getTagId()).orElseThrow(() -> new RuntimeException("Tag not found"));
            task.setTag(tag);
        }
        if(taskDTO.getGroupId() != null){
            Group group = groupRepository.findById(taskDTO.getGroupId()).orElseThrow(() -> new RuntimeException("Group not found"));
            task.setGroup(group);
        }

        return task;
    }

    public TaskDTO convertTaskIntoTaskDTO(Task task){
        TaskDTO taskDTO = new TaskDTO();

        taskDTO.setId(task.getId());
        taskDTO.setTaskName(task.getTaskName());
        taskDTO.setStartTime(task.getStartTime());
        taskDTO.setEndTime(task.getEndTime());
        taskDTO.setTaskType(task.getTaskType());
        if(task.getGroup() != null){
            taskDTO.setGroupId(task.getGroup().getId());
        }

        if(task.getTag() != null){
            taskDTO.setTagId(task.getTag().getId());
        }

        taskDTO.setIsDone(task.getTotalIsDone());

        return taskDTO;
    }

}
