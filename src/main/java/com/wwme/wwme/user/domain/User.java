package com.wwme.wwme.user.domain;

import com.wwme.wwme.group.domain.UserGroup;
import com.wwme.wwme.task.domain.UserTask;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Data
@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "user_table")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Column(name = "nickname")
    private String nickname;
    private LocalDateTime registerDate;

    private String userKey;
    private String socialProvider;
    private String role;

    private Long profileImageId; //added profile photo info

    //Cascade : 부모 (one side) 가 없어지면 자식도 모두 사라진다.
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    List<UserTask> userTaskList = new ArrayList<>();

    @OneToMany(mappedBy = "user")
    Collection<UserGroup> userGroup;

    public User(String nickname) {
        this.nickname = nickname;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", nickname='" + nickname + '\'' +
                ", registerDate=" + registerDate +
                ", userKey='" + userKey + '\'' +
                ", socialProvider='" + socialProvider + '\'' +
                ", role='" + role + '\'' +
                ", profileImageId=" + profileImageId +
                '}';
    }

    public void ChangeNickname(String changedName) {
        this.nickname = changedName;
    }

    public void registerComplete() {
        this.registerDate = LocalDateTime.now();
    }

}
