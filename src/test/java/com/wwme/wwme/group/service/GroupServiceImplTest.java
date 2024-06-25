package com.wwme.wwme.group.service;


import com.wwme.wwme.group.domain.Group;
import com.wwme.wwme.user.domain.User;
import com.wwme.wwme.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Transactional
class GroupServiceImplTest {
    @Autowired
    private GroupInvitationService groupInvitationService;

    @Autowired
    private GroupService groupService;

    @Autowired
    private UserRepository userRepository;


    @Test
    void createGroupWithUserAndColorSuccess() {
        User user = userRepository.save(new User());
        String groupName = "someName";
        Long color = 0L;

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
    void createGroupWithUserAndColorFail_GroupNameTooLong() {
        User user = userRepository.save(new User());
        String groupName = "Very long group name that exceeds 20 char";
        Long color = 0L;

        assertThrows(IllegalArgumentException.class, () -> {
            groupService.createGroupWithUserAndColor(groupName, user, color);
        });
    }


    @Test
    void updateGroupNameAndColorSuccess() {
        User user = userRepository.save(new User());
        String groupName = "someName";
        Long color = 0L;
        Group newGroup = groupService.createGroupWithUserAndColor(groupName, user, color);

        String newGroupName = "newName";
        Long newColor = 1L;

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
        Long color = 0L;

        Group newGroup = groupService.createGroupWithUserAndColor(groupName, user, color);

        String newGroupName = "newName";
        Long newColor = 1L;

        assertThrows(NoSuchElementException.class, () -> {
            User user2 = new User();
            user2.setId(user.getId() + 1);
            groupService.updateGroupNameAndColor(newGroup.getId(), newGroupName, newColor, user2);
        });
    }

    @Test
    void updateGroupNameAndColorFail_NonExistentGroupId() {
        User user = userRepository.save(new User());

        String groupName = "someName";
        Long color = 0L;

        Group newGroup = groupService.createGroupWithUserAndColor(groupName, user, color);

        String newGroupName = "newName";
        Long newColor = 1L;

        assertThrows(NoSuchElementException.class, () -> {
            groupService.updateGroupNameAndColor(newGroup.getId() + 1, newGroupName, newColor, user);
        });
    }

    @Test
    void updateGroupNameAndColorFail_NonExistentUserGroup() {
        User user = userRepository.save(new User());

        String groupName = "someName";
        Long color = 0L;

        Group newGroup = groupService.createGroupWithUserAndColor(groupName, user, color);

        String newGroupName = "newName";
        Long newColor = 1L;

        assertThrows(NoSuchElementException.class, () -> {
            User user2 = userRepository.save(new User());
            // there is no relationship between newGroup and user2
            // so no UserGroup is present
            groupService.updateGroupNameAndColor(newGroup.getId(), newGroupName, newColor, user2);
        });
    }

    @Test
    void updateGroupNameAndColorFail_GroupNameTooLong() {
        User user = userRepository.save(new User());

        String groupName = "someName";
        Long groupColor = 5L;

        Group group = groupService.createGroupWithUserAndColor(groupName, user, groupColor);
        String newGroupName = "this is a very long group name that exceeds max";

        assertThrows(IllegalArgumentException.class, () -> {
            groupService.updateGroupNameAndColor(group.getId(), newGroupName, groupColor, user);
        });
    }

    @Test
    void getAllUserFromGroupIdSuccess() {
        User user = userRepository.save(new User());
        String groupName = "somename";
        Long groupColor = 5L;
        Group group = groupService.createGroupWithUserAndColor(groupName, user, groupColor);

        User user2 = groupService.getAllUserFromGroupId(group.getId()).get(0);
        assertThat(user2).isEqualTo(user);
    }

    @Test
    void getAllUserFromGroupIdFail_NoGroupFound() {
        assertThrows(NoSuchElementException.class, () -> {
            groupService.getAllUserFromGroupId(-1);
        });
    }

    @Test
    void getAllUserFromGroupSuccess() {
        User user = userRepository.save(new User());
        String groupName = "somename";
        Long groupColor = 5L;

        Group group = groupService.createGroupWithUserAndColor(groupName, user, groupColor);
        User user2 = groupService.getAllUserFromGroup(group).get(0);
        assertThat(user2).isEqualTo(user);
    }

    @Test
    void getAllUserFromGroupSuccessMultipleUser() {
        User user = userRepository.save(new User());
        User user2 = userRepository.save(new User());
        String groupName = "somename";
        Long groupColor = 5L;

        Group group = groupService.createGroupWithUserAndColor(groupName, user, groupColor);
        String code = groupInvitationService.createGroupInvitation(group);
        groupInvitationService.acceptInvitation(code, user2, groupColor);


        List<User> allUserFromGroup = groupService.getAllUserFromGroup(group);
        assertThat(allUserFromGroup)
                .contains(user)
                .contains(user2);
    }

    @Test
    void getGroupCodeSuccess() {
        User user = userRepository.save(new User());
        String groupName = "somename";
        Long groupColor = 5L;

        Group group = groupService.createGroupWithUserAndColor(groupName, user, groupColor);
        String code = groupInvitationService.createGroupInvitation(group);
        String gotCode = groupService.getGroupCode(group.getId());

        assertThat(code).isEqualTo(gotCode);
    }

    @Test
    void getGroupCodeFail_NoGroupInvitation() {
        User user = userRepository.save(new User());
        String groupName = "somename";
        Long groupColor = 5L;

        Group group = groupService.createGroupWithUserAndColor(groupName, user, groupColor);
        assertThrows(NoSuchElementException.class, () -> {
            groupService.getGroupCode(group.getId());
        });
    }

    @Test
    void getGroupByCodeSuccess() {
        User user = userRepository.save(new User());
        String groupName = "somename";
        Long groupColor = 5L;

        Group group = groupService.createGroupWithUserAndColor(groupName, user, groupColor);
        String code = groupInvitationService.createGroupInvitation(group);

        Group foundGroup = groupService.getGroupByCode(code);

        assertThat(foundGroup).isEqualTo(group);
    }

    @Test
    void getGroupByCodeFail_NoGroupFoundWithCode() {
        User user = userRepository.save(new User());
        String groupName = "somename";
        Long groupColor = 5L;

        Group group = groupService.createGroupWithUserAndColor(groupName, user, groupColor);
        groupInvitationService.createGroupInvitation(group);

        // Always fails since it is 3 characters
        assertThrows(NoSuchElementException.class, () -> {
            groupService.getGroupByCode("abc");
        });
    }
}