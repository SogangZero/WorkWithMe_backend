package com.wwme.wwme.group.service;


import com.wwme.wwme.group.domain.Group;
import com.wwme.wwme.group.domain.UserGroup;
import com.wwme.wwme.user.domain.User;
import com.wwme.wwme.user.repository.UserRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
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
        assertThat(newGroup.getUserGroup())
                .hasSize(1)
                .anySatisfy(userGroup -> {
                            assertThat(userGroup.getUser()).isEqualTo(user);
                            assertThat(userGroup.getGroup()).isEqualTo(newGroup);
                            assertThat(userGroup.getColor()).isEqualTo(color);
                        }
                );
    }
}