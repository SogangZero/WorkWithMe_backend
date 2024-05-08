package com.wwme.wwme.user.domain;

import com.wwme.wwme.group.domain.UserGroup;
import com.wwme.wwme.task.domain.Task;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Getter
@Setter
@Entity
@NoArgsConstructor
@Table(name = "user_table")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String nickname;
    private LocalDateTime register_date;
    private String social_provider;

    @ManyToMany
    List<Task> task_list;

    @OneToMany(mappedBy = "user")
    Collection<UserGroup> userGroup;

    public User(String nickname) {
        this.nickname = nickname;
    }
}
