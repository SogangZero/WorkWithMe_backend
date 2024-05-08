package com.wwme.wwme.group.repository;

import com.wwme.wwme.group.domain.Group;
import com.wwme.wwme.group.domain.UserGroup;
import com.wwme.wwme.user.domain.User;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class UserGroupRepositoryTest {
    private final UserGroupRepository userGroupRepository;
    private final GroupRepository groupRepository;

    @Autowired
    public UserGroupRepositoryTest(
            UserGroupRepository userGroupRepository,
            GroupRepository groupRepository
    ) {
        this.userGroupRepository = userGroupRepository;
        this.groupRepository = groupRepository;
    }

    @Test
    void userGroupSaveTest() {
        Group g1 = new Group("group111");
        User u1 = new User();
        UserGroup ug1 = new UserGroup();
        ug1.setGroup(g1);
        ug1.setUser(u1);

        ug1 = userGroupRepository.save(ug1);
        UserGroup foundUserGroup = userGroupRepository.findById(ug1.getId()).get();

        assertThat(foundUserGroup.getUser().getId()).isEqualTo(u1.getId());
        assertThat(foundUserGroup.getGroup().getId()).isEqualTo(g1.getId());
        assertThat(foundUserGroup.getId()).isEqualTo(ug1.getId());
    }

}