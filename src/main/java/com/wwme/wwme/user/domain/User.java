package com.wwme.wwme.user.domain;

import com.wwme.wwme.group.domain.UserGroup;
import com.wwme.wwme.task.domain.Task;
import com.wwme.wwme.task.domain.UserTask;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
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

    private String userKey;
    private String social_provider;
    public String role;

    //Cascade : 부모 (one side) 가 없어지면 자식도 모두 사라진다.
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    List<UserTask> userTaskList = new ArrayList<>();

    @OneToMany(mappedBy = "user")
    Collection<UserGroup> userGroup;

    public User(String nickname) {
        this.nickname = nickname;
    }
}
