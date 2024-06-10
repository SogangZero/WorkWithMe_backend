package com.wwme.wwme.group.service;

import com.wwme.wwme.group.domain.Group;
import com.wwme.wwme.group.domain.UserGroup;
import com.wwme.wwme.group.repository.GroupRepository;
import com.wwme.wwme.group.repository.UserGroupRepository;
import com.wwme.wwme.user.domain.User;
import com.wwme.wwme.user.repository.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Transactional
public class UserGroupServiceImplTest {
    @Autowired
    UserRepository userRepository;

    @Autowired
    GroupRepository groupRepository;

    @Autowired
    UserGroupRepository userGroupRepository;

    @Autowired
    GroupService groupService;

    @Autowired
    UserGroupService userGroupService;

    @Test
    void getUserGroupByIdAndUserSuccess() {
        User user = userRepository.save(new User());
        String groupName = "someName";
        String color = "FAFAFA";
        Group newGroup = groupService.createGroupWithUserAndColor(groupName, user, color);

        UserGroup userGroup = assertDoesNotThrow(
                () -> userGroupService.getUserGroupByIdAndUser(newGroup.getId(), user)
        );

        assertThat(userGroup.getGroup()).isEqualTo(newGroup);
        assertThat(userGroup.getUser()).isEqualTo(user);
    }

    @Test
    void getUserGroupByIdAndUserFail_InvalidGroupId() {
        User user = userRepository.save(new User());

        assertThrows(
                NoSuchElementException.class,
                () -> userGroupService.getUserGroupByIdAndUser(0, user)
        );
    }

    @Test
    void getUserGroupByIdAndUserFail_NoUserGroup() {
        User user = userRepository.save(new User());
        Group group = groupRepository.save(new Group());

        assertThrows(
                NoSuchElementException.class,
                () -> userGroupService.getUserGroupByIdAndUser(group.getId(), user)
        );
    }

    @Test
    void getAllUserGroupOfUserSuccess() {
        User user = userRepository.save(new User());

        String groupName1 = "gn1";
        String groupColor1 = "CCCCCC";
        Group group1 = groupService.createGroupWithUserAndColor(groupName1, user, groupColor1);

        String groupName2 = "gn2";
        String groupColor2 = "BBBBBB";
        Group group2 = groupService.createGroupWithUserAndColor(groupName2, user, groupColor2);

        Collection<UserGroup> userGroups = userGroupService.getAllUserGroupOfUser(user);
        assertThat(userGroups)
                .satisfiesExactlyInAnyOrder(
                        userGroup -> assertThat(userGroup.getGroup()).isEqualTo(group1),
                        userGroup -> assertThat(userGroup.getGroup()).isEqualTo(group2)
                );
    }

    @Test
    void addUserToGroupWithColorSuccess() {
        String groupName = "abcdefgh";
        String groupColor = "FAFAFA";
        String groupColor2 = "AFAFAF";
        User user = userRepository.save(new User());
        User user2 = userRepository.save(new User());
        Group group = groupService.createGroupWithUserAndColor(groupName, user, groupColor);
        UserGroup userGroup =
                assertDoesNotThrow(() -> userGroupService.addUserToGroupWithColor(group, user2, groupColor2));

        assertThat(userGroup.getUser()).isEqualTo(user2);
        assertThat(userGroup.getGroup()).isEqualTo(group);
        assertThat(userGroup.getColor()).isEqualTo(groupColor2);
    }

    @Test
    void addUserToGroupWithColorFail_UserAlreadyInGroup() {
        String groupName = "abcdefgh";
        String groupColor = "FAFAFA";
        String groupColor2 = "AFAFAF";
        User user = userRepository.save(new User());
        Group group = groupService.createGroupWithUserAndColor(groupName, user, groupColor);
        assertThrows(
                IllegalArgumentException.class,
                () -> userGroupService.addUserToGroupWithColor(group, user, groupColor2)
        );
    }

    @Test
    void addUserToGroupWithColorFail_NotSavedUser() {
        String groupName = "abcdefgh";
        String groupColor = "FAFAFA";
        String groupColor2 = "AFAFAF";
        User user = userRepository.save(new User());
        User user2 = new User();
        Group group = groupService.createGroupWithUserAndColor(groupName, user, groupColor);

        assertThrows(
                InvalidDataAccessApiUsageException.class,
                () -> userGroupService.addUserToGroupWithColor(group, user2, groupColor2)
        );
    }

    @Test
    void removeUserFromGroupSuccess() {
        User user = userRepository.save(new User());
        Group group = groupRepository.save(new Group());
        UserGroup userGroup = new UserGroup();
        userGroup.setGroup(group);
        userGroup.setUser(user);
        userGroup = userGroupRepository.save(userGroup);

        userGroupService.removeUserFromGroup(group.getId(), user);

        assertThat(userGroupRepository.existsById(userGroup.getId())).isEqualTo(false);
    }

    @Test
    void removeUserFromGroupFail_NoGroup() {
        User user = userRepository.save(new User());
        Group group = groupRepository.save(new Group());
        UserGroup userGroup = new UserGroup();
        userGroup.setGroup(group);
        userGroup.setUser(user);
        userGroupRepository.save(userGroup);

        assertThrows(NoSuchElementException.class,
                () -> userGroupService.removeUserFromGroup(group.getId()-1, user));
    }

    @Test
    void removeUserFromGroupFail_NoUserGroup() {
        User user = userRepository.save(new User());
        Group group = groupRepository.save(new Group());

        assertThrows(NoSuchElementException.class,
                () -> userGroupService.removeUserFromGroup(group.getId(), user));

    }
}
