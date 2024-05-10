package com.wwme.wwme.group.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Collection;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "group_table")
public class Group {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String groupName;

    @OneToMany(mappedBy = "group")
    Collection<UserGroup> userGroup = new ArrayList<>();

    public Group(String groupName) {
        this.groupName = groupName;
    }
}
