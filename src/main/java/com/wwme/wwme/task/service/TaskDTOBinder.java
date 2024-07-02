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
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
@Slf4j
public class TaskDTOBinder {
    private final TaskRepository taskRepository;



    public CUTaskSendDTO bindCUTaskSendDTO(Task task, User loginUser) {
        //checking is_done_total
        boolean task_is_done_total = setAndReturnIsDoneTotal(task);
        boolean task_is_done_personal = setAndReturnIsDonePersonal(task, loginUser);

        return CUTaskSendDTO.builder()
                .task_id(task.getId())
                .task_name(task.getTaskName())
                .group(bindGroupInfo(task.getGroup()))
                .tag(bindTagInfo(task.getTag()))
                .is_done_count(task.countDoneUser())
                .is_done_personal(task_is_done_personal)
                .is_done_total(task_is_done_total)
                .user_list(bindUserListInfo(task.getUserTaskList()))
                .task_type(task.getTaskType())
                .start_time(task.getStartTime())
                .end_time(task.getEndTime())
                .build();
    }

    private boolean setAndReturnIsDonePersonal(Task task, User loginUser) {
        UserTask ut = task.getUserTaskList().stream()
                .filter(s -> s.getUser().getId().equals(loginUser.getId()))
                .findAny().orElseThrow(
                        ()-> new NoSuchElementException("UserTask of UserID : "+loginUser.getId()
                        + "Does not Exist. In function setAndReturnIsDonePersonal")
                );
        return ut.getIsDone();
    }

    public boolean setAndReturnIsDoneTotal(Task task){
        log.info(task.getTaskType());
        switch (task.getTaskType()){
            case "personal":
                task.setTotalIsDone(task.getUserTaskList().get(0).getIsDone());
                break;
            case "anyone":
                for (UserTask ut : task.getUserTaskList()){
                    log.info(ut.getUser() + " : "+ ut.getIsDone());
                }

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
        Long tag_id;
        String tag_name;
        if(tag==null){
            tag_id = null;
            tag_name = null;
        }else{
            tag_id = tag.getId();
            tag_name = tag.getTagName();
        }

        return CUTaskTagDTO.builder()
                .tag_id(tag_id)
                .tag_name(tag_name)
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
