package com.wwme.wwme.group.domain;

import com.wwme.wwme.user.domain.User;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.Collection;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "group_table")
@Builder
@AllArgsConstructor
public class Group {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String groupName;

    @OneToMany(mappedBy = "group")
    Collection<UserGroup> userGroupList = new ArrayList<>();

    public Group(String groupName) {
        this.groupName = groupName;
    }

    public void addUserGroup(UserGroup userGroup) {
        this.userGroupList.add(userGroup);
    }

    public boolean existUser(User user) {
        for (UserGroup userGroup : userGroupList) {
            if (userGroup.getUser().equals(user)) {
                return true;
            }
        }

        return false;
    }

    public boolean existUserById(Long userId) {
        for (UserGroup userGroup : userGroupList) {
            if (userGroup.getUser().getId().equals(userId)) {
                return true;
            }
        }

        return false;
    }

    public Integer getUserNumInGroup() {
        return this.userGroupList.size();
    }
}
