package com.wwme.wwme.group.repository;

import com.wwme.wwme.group.domain.Group;
import com.wwme.wwme.group.domain.UserGroup;
import com.wwme.wwme.user.domain.User;
import com.wwme.wwme.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class UserGroupRepositoryTest {
    private final UserGroupRepository userGroupRepository;
    private final GroupRepository groupRepository;
    private final UserRepository userRepository;

    @Autowired
    public UserGroupRepositoryTest(
            UserGroupRepository userGroupRepository,
            GroupRepository groupRepository,
            UserRepository userRepository
    ) {
        this.userGroupRepository = userGroupRepository;
        this.groupRepository = groupRepository;
        this.userRepository = userRepository;
    }

    @Test
    void userGroupSaveTest() {
        Group g1 = new Group("group111");
        g1 = groupRepository.save(g1);
        User u1 = new User("user1");
        u1 = userRepository.save(u1);

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