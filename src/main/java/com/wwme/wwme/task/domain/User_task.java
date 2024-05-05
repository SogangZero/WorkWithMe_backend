package com.wwme.wwme.task.domain;

import com.wwme.wwme.user.User;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import org.hibernate.annotations.ColumnDefault;

public class User_task {

    @EmbeddedId
    private Long id;

    @ManyToOne
    @MapsId("user_id")
    @JoinColumn(name = "user_id")
    User user;

    @ManyToOne
    @MapsId("task_id")
    @JoinColumn(name = "task_id")
    Task task;

    Boolean is_done = false;
}
