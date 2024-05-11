package com.wwme.wwme.group.service;


import com.wwme.wwme.group.domain.Group;
import com.wwme.wwme.user.domain.User;
import com.wwme.wwme.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
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
}