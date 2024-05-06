package com.wwme.wwme.group.domain;

import com.wwme.wwme.user.domain.User;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class UserGroup {
    @Id
    private Long id;

    @Embedded
    Color color;

    @ManyToOne
    User user;

    @ManyToOne
    Group group;
}
