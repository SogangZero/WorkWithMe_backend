package com.wwme.wwme.group.service;


import com.wwme.wwme.group.domain.Group;
import com.wwme.wwme.user.domain.User;
import com.wwme.wwme.user.repository.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class GroupServiceImplTest {
    @Autowired
    private GroupService groupService;

    @Autowired
    private UserRepository userRepository;


    @Test
    @Transactional
    void createGroupWithUserAndColorSuccess() {
        User user = userRepository.save(new User());
        String groupName = "someName";
        String color = "FFFFFF";

        Group newGroup = groupService.createGroupWithUserAndColor(groupName, user, color);

        assertThat(newGroup.getGroupName()).isEqualTo(groupName);
        assertThat(newGroup.getUserGroupList())
                .hasSize(1)
                .anySatisfy(userGroup -> {
                            assertThat(userGroup.getUser()).isEqualTo(user);
                            assertThat(userGroup.getGroup()).isEqualTo(newGroup);
                            assertThat(userGroup.getColor()).isEqualTo(color);
                        }
                );
    }

    @Test
    void updateGroupNameAndColorSuccess() {
        User user = userRepository.save(new User());
        String groupName = "someName";
        String color = "FAFAFA";
        Group newGroup = groupService.createGroupWithUserAndColor(groupName, user, color);

        String newGroupName = "newName";
        String newColor = "ABABAB";

        Group updatedGroup = groupService.updateGroupNameAndColor(newGroup.getId(), newGroupName, newColor, user);

        assertThat(updatedGroup.getGroupName()).isEqualTo(newGroupName);
        assertThat(updatedGroup.getUserGroupList())
                .hasSize(1)
                .anySatisfy(userGroup -> {
                            assertThat(userGroup.getUser()).isEqualTo(user);
                            assertThat(userGroup.getGroup()).isEqualTo(updatedGroup);
                            assertThat(userGroup.getColor()).isEqualTo(newColor);
                        }
                );
    }

    @Test
    void updateGroupNameAndColorFail_NonExistentUser() {
        User user = userRepository.save(new User());

        String groupName = "someName";
        String color = "FAFAFA";

        Group newGroup = groupService.createGroupWithUserAndColor(groupName, user, color);

        String newGroupName = "newName";
        String newColor = "ABABAB";

        Assertions.assertThrows(NoSuchElementException.class, () -> {
            User user2 = new User();
            user2.setId(user.getId()+1);
            groupService.updateGroupNameAndColor(newGroup.getId(), newGroupName, newColor, user2);
        });
    }

    @Test
    void updateGroupNameAndColorFail_NonExistentGroupId() {
        User user = userRepository.save(new User());

        String groupName = "someName";
        String color = "FAFAFA";

        Group newGroup = groupService.createGroupWithUserAndColor(groupName, user, color);

        String newGroupName = "newName";
        String newColor = "ABABAB";

        Assertions.assertThrows(NoSuchElementException.class, () -> {
            groupService.updateGroupNameAndColor(newGroup.getId()+1, newGroupName, newColor, user);
        });
    }

    @Test
    void updateGroupNameAndColorFail_NonExistentUserGroup() {
        User user = userRepository.save(new User());

        String groupName = "someName";
        String color = "FAFAFA";

        Group newGroup = groupService.createGroupWithUserAndColor(groupName, user, color);

        String newGroupName = "newName";
        String newColor = "ABABAB";

        Assertions.assertThrows(NoSuchElementException.class, () -> {
            User user2 = userRepository.save(new User());
            // there is no relationship between newGroup and user2
            // so no UserGroup is present
            groupService.updateGroupNameAndColor(newGroup.getId(), newGroupName, newColor, user2);
        });
    }


}