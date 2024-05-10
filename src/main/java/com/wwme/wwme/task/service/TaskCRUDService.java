package com.wwme.wwme.task.service;

import com.wwme.wwme.group.domain.Group;
import com.wwme.wwme.group.repository.GroupRepository;
import com.wwme.wwme.task.domain.DTO.TaskDTO;
import com.wwme.wwme.task.domain.Tag;
import com.wwme.wwme.task.domain.Task;
import com.wwme.wwme.task.repository.TagRepository;
import com.wwme.wwme.task.repository.TaskRepository;
import com.wwme.wwme.task.repository.UserTaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TaskCRUDService {

    TaskRepository taskRepository;
    TagRepository tagRepository;
    UserTaskRepository userTaskRepository;
    GroupRepository groupRepository;



    public Task createUpdateTask(TaskDTO taskDTO){
        Task task = convertTaskDTOIntoTask(taskDTO);
        return taskRepository.save(task);
    }

    public TaskDTO readOneTask(Long task_id){
        Task task = taskRepository.findById(task_id).orElseThrow(()-> new RuntimeException("Task from Task ID not found"));

        return convertTaskIntoTaskDTO(task);
    }

    public List<TaskDTO> readTaskListByGroup(TaskDTO taskDTO){
        return null;
    }

    public List<TaskDTO> readTaskListByUser(TaskDTO taskDTO){
        return null;
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
        task.setTask_name(taskDTO.getTask_name());
        task.setStart_time(taskDTO.getStart_time());
        task.setEnd_time(taskDTO.getEnd_time());
        task.setTask_type(taskDTO.getTask_type());
        task.setTotal_is_done(taskDTO.getIs_done());

        if(taskDTO.getTag_id() != null){
            Tag tag = tagRepository.findById(taskDTO.getTag_id()).orElseThrow(() -> new RuntimeException("Tag not found"));
            task.setTag(tag);
        }else{
            throw new RuntimeException("No Tag Id in taskDTO");
        }
        if(taskDTO.getGroup_id() != null){
            Group group = groupRepository.findById(taskDTO.getGroup_id()).orElseThrow(() -> new RuntimeException("Group not found"));
            task.setGroup(group);
        }else{
            throw new RuntimeException("No Group Id in taskDTO");
        }

        return task;
    }

    public TaskDTO convertTaskIntoTaskDTO(Task task){
        TaskDTO taskDTO = new TaskDTO();

        taskDTO.setId(task.getId());
        taskDTO.setTask_name(task.getTask_name());
        taskDTO.setStart_time(task.getStart_time());
        taskDTO.setEnd_time(task.getEnd_time());
        taskDTO.setTask_type(task.getTask_type());
        taskDTO.setGroup_id(task.getGroup().getId());
        taskDTO.setTag_id(task.getTag().getId());
        taskDTO.setIs_done(task.getTotal_is_done());

        return taskDTO;
    }

}
