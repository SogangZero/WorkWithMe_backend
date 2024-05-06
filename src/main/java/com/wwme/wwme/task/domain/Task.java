package com.wwme.wwme.task.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

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

    @ManyToOne
    @JoinColumn(name = "tag_id")
    private Tag tag;

    private Long group_id;
}
