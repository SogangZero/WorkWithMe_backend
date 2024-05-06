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

    @Embedded
    Color color;

    @ManyToOne
    User user;

    @ManyToOne
    Group group;

}
