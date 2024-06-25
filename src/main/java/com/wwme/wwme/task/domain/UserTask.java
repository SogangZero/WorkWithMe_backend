package com.wwme.wwme.task.domain;

import com.wwme.wwme.user.domain.User;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@ToString
@Builder
@RequiredArgsConstructor
@AllArgsConstructor
public class UserTask {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "task_id")
    Task task;

    Boolean isDone;
}
