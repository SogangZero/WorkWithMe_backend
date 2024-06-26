package com.wwme.wwme.task.service;

import com.wwme.wwme.group.domain.Group;
import com.wwme.wwme.task.domain.DTO.sendDTO.CUTaskGroupDTO;
import com.wwme.wwme.task.domain.DTO.sendDTO.CUTaskSendDTO;
import com.wwme.wwme.task.domain.DTO.sendDTO.CUTaskTagDTO;
import com.wwme.wwme.task.domain.DTO.sendDTO.CUTaskUserDTO;
import com.wwme.wwme.task.domain.Tag;
import com.wwme.wwme.task.domain.Task;
import com.wwme.wwme.task.domain.UserTask;
import com.wwme.wwme.task.repository.TaskRepository;
import com.wwme.wwme.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TaskDTOBinder {
    private final TaskRepository taskRepository;



    public CUTaskSendDTO bindCUTaskSendDTO(Task task) {
        //checking is_done_total
        boolean task_is_done_total = setAndReturnIsDoneTotal(task);

        return CUTaskSendDTO.builder()
                .task_id(task.getId())
                .task_name(task.getTaskName())
                .group(bindGroupInfo(task.getGroup()))
                .tag(bindTagInfo(task.getTag()))
                .is_done_count(task.countDoneUser())
                .is_done_personal(task.isDonePersonal())
                .is_done_total(task_is_done_total)
                .user_list(bindUserListInfo(task.getUserTaskList()))
                .task_type(task.getTaskType())
                .start_time(task.getStartTime())
                .end_time(task.getEndTime())
                .build();
    }
    public boolean setAndReturnIsDoneTotal(Task task){
        switch (task.getTaskType()){
            case "personal":
                task.setTotalIsDone(task.getUserTaskList().get(0).getIsDone());
                break;
            case "anyone":
                boolean anyIsDone = task.getUserTaskList().stream()
                        .anyMatch(UserTask::getIsDone);
                task.setTotalIsDone(anyIsDone);
                break;
            case "group":
                boolean allIsDone = task.getUserTaskList().stream()
                        .allMatch(UserTask::getIsDone);
                task.setTotalIsDone(allIsDone);
                break;
            default:
                throw new IllegalArgumentException("Task type is invalid: "+task.getTaskType()
                + "in function checkAndSetIsDoneTotal");
        }
        return taskRepository.save(task).getTotalIsDone();
    }

    private List<CUTaskUserDTO> bindUserListInfo(List<UserTask> userTaskList) {
        ArrayList<CUTaskUserDTO> users = new ArrayList<>();
        for (UserTask userTask : userTaskList) {
            User user = userTask.getUser();

            CUTaskUserDTO element = CUTaskUserDTO.builder()
                    .user_id(user.getId())
                    .nickname(user.getNickname())
                    .profile_image_id(user.getProfileImageId())
                    .is_done(userTask.getIsDone())
                    .build();

            users.add(element);
        }

        return users;
    }

    private CUTaskTagDTO bindTagInfo(Tag tag) {
        return CUTaskTagDTO.builder()
                .tag_id(tag.getId())
                .tag_name(tag.getTagName())
                .build();
    }

    private CUTaskGroupDTO bindGroupInfo(Group group) {
        return CUTaskGroupDTO.builder()
                .group_id(group.getId())
                .group_name(group.getGroupName())
                .group_color("TEMPCOLOR")
                .num_people(group.getUserNumInGroup())
                .build();
    }
}
