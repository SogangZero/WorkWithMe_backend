package com.wwme.wwme.task.domain;

import com.wwme.wwme.group.domain.Group;
import com.wwme.wwme.user.domain.User;
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
    private String task_name;
    private LocalDateTime start_time;
    private LocalDateTime end_time;
    private String task_type;
    private Boolean total_is_done;
    //건의사항 : 완료한 날짜/시간를 넣자

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "tag_id")
    private Tag tag;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "group_id")
    private Group group;

    @OneToMany(mappedBy = "task", cascade = CascadeType.ALL, orphanRemoval = true) //the userTask entity controlls the relationship, and there is a "task" field in usertask
    private List<UserTask> userTaskList = new ArrayList<>();
}
