package com.wwme.wwme.group.service;

import com.wwme.wwme.group.domain.Group;
import com.wwme.wwme.group.domain.GroupInvitation;
import com.wwme.wwme.group.domain.UserGroup;
import com.wwme.wwme.group.repository.GroupInvitationRepository;
import com.wwme.wwme.group.repository.UserGroupRepository;
import com.wwme.wwme.user.domain.User;
import com.wwme.wwme.user.repository.UserRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Transactional
public class GroupInvitationServiceImplTest {
    @Autowired
    GroupService groupService;

    @Autowired
    GroupInvitationService groupInvitationService;

    @Autowired
    UserRepository userRepository;

    @Autowired
    UserGroupRepository userGroupRepository;

    @Autowired
    GroupInvitationRepository groupInvitationRepository;

    @Test
    void createGroupInvitationSuccess() {
        String groupName = "groupName";
        String groupColor = "123456";
        User user = userRepository.save(new User());
        Group group = groupService.createGroupWithUserAndColor(groupName, user, groupColor);

        String code = groupInvitationService.createGroupInvitation(group);

        Optional<GroupInvitation> groupInvitationOptional = groupInvitationRepository.findByCode(code);
        GroupInvitation groupInvitation = groupInvitationOptional.orElse(null);

        assertThat(groupInvitationOptional).isPresent();
        assertThat(groupInvitation.getCode()).isEqualTo(code);
        assertThat(groupInvitation.getGroup()).isEqualTo(group);
    }

    @Test
    void createGroupInvitationFail_GroupNotFoundInDB() {
        Group group = new Group();
        assertThrows(
                Exception.class,
                () -> groupInvitationService.createGroupInvitation(group)
        );
    }

    @Test
    void acceptInvitationSuccess() {
        String groupName = "group1";
        String groupColor = "ABABAB";
        String groupColor2 = "BCBCBC";

        User user = userRepository.save(new User());
        Group group = groupService.createGroupWithUserAndColor(groupName, user, groupColor);

        String code = groupInvitationService.createGroupInvitation(group);

        User user2 = userRepository.save(new User());

        group = groupInvitationService.acceptInvitation(code, user2, groupColor2);

        Optional<UserGroup> userGroupOptional = userGroupRepository.findByUserAndGroup(user2, group);
        UserGroup userGroup = userGroupOptional.orElse(null);

        assertThat(userGroupOptional).isPresent();
        assertThat(userGroup.getGroup()).isEqualTo(group);
        assertThat(userGroup.getUser()).isEqualTo(user2);
        assertThat(userGroup.getColor()).isEqualTo(groupColor2);
    }

    @Test
    void acceptInvitationFail_InvalidGroupInvitation() {
        User user = userRepository.save(new User());
        assertThrows(
                NoSuchElementException.class,
                () -> groupInvitationService.acceptInvitation("non-existent-code", user, "AFAFAF")
        );
    }

    @Test
    void acceptInvitationFail_UserAlreadyInGroup() {
        String groupName = "group1";
        String groupColor = "ABABAB";
        String groupColor2 = "BCBCBC";

        User user = userRepository.save(new User());
        Group group = groupService.createGroupWithUserAndColor(groupName, user, groupColor);
        String code = groupInvitationService.createGroupInvitation(group);

        assertThrows(
                IllegalArgumentException.class,
                () ->  groupInvitationService.acceptInvitation(code, user, groupColor2)
        );
    }
}
