package com.wwme.wwme.user;

import com.wwme.wwme.task.domain.Task;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Entity
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String nickname;
    private LocalDateTime register_date;
    private String social_provider;

    @ManyToMany
    List<Task> task_list;
}
