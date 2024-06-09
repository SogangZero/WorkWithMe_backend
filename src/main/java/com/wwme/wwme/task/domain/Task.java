package com.wwme.wwme.task.domain;

import com.wwme.wwme.group.domain.Group;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@ToString
@RequiredArgsConstructor
public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String taskName;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String taskType;
    private Boolean totalIsDone;
    //건의사항 : 완료한 날짜/시간를 넣자

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tag_id")
    private Tag tag;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id")
    private Group group;

    @OneToMany(mappedBy = "task", cascade = CascadeType.ALL, orphanRemoval = true) //the userTask entity controlls the relationship, and there is a "task" field in usertask
    private List<UserTask> userTaskList = new ArrayList<>();
}
