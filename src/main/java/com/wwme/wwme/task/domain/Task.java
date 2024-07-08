package com.wwme.wwme.task.domain;

import com.wwme.wwme.group.domain.Group;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@ToString
@RequiredArgsConstructor
@AllArgsConstructor
@Builder
public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String taskName;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String taskType;
    private Boolean totalIsDone;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn
    private Tag tag;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn
    private Group group;

    @Builder.Default
    @OneToMany(mappedBy = "task", cascade = CascadeType.ALL) //the userTask entity controls the relationship, and there is a "task" field in usertask
    private List<UserTask> userTaskList = new ArrayList<>();

    public void addUserTask(UserTask userTask) {
        this.userTaskList.add(userTask);
    }

    public void changeTag(Tag tag) {
        this.tag = tag;
    }

    public void changeEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public boolean isBelongToGroupId(Long id) {
        return id.equals(this.getGroup().getId());
    }

    public boolean validateTaskType(String taskType) {
        if (taskType == null) return false;
        return taskType.equals("personal") || taskType.equals("group") || taskType.equals("anyone");
    }

    public void changeTaskType(String taskType) {
        if (!validateTaskType(taskType)) {
            throw new IllegalArgumentException("Tag name error");
        }
        this.taskType = taskType;
    }

    public boolean validateTagIdInGroup(Tag tag) {
        return this.getGroup().equals(tag.getGroup());
    }

//    public Boolean isDonePersonal() {
////        if (!this.taskType.equals("personal")) return false;
////        return this.userTaskList.get(0).getIsDone();
//        UserTask ut = this.getUserTaskList().stream()
//                .filter(s -> s.user.getId().equals())
//    }

//    public Boolean isDoneTotal() { //TODO : check for anyone task type
//        return userTaskList.stream()
//                .allMatch(UserTask::getIsDone);
//    }

    public Integer countDoneUser() {
        return (int) userTaskList.stream()
                .filter(UserTask::getIsDone)
                .count();
    }
}
