package com.wwme.wwme.group.domain;

import com.wwme.wwme.user.domain.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class UserGroup {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    Long color;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    User user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    Group group;

    public void setGroup(Group group) {
        group.addUserGroup(this);
        this.group = group;
    }
}
