package com.wwme.wwme.task.domain;

import com.wwme.wwme.user.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
@ToString
public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String task_name;
    private LocalDateTime start_time;
    private LocalDateTime end_time;
    private String task_type;
    private Boolean total_is_done;

    @ManyToOne
    @JoinColumn(name = "tag_id")
    private Tag tag;

    private Long group_id;
}
