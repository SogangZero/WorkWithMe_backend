package com.wwme.wwme.group.domain;

import com.wwme.wwme.user.domain.User;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;

public class GroupTest {

    public Group group;

    @BeforeEach
    public void init() {
        group = Group.builder()
                .id(1L)
                .groupName("TestGroup")
                .userGroupList(new ArrayList<>())
                .build();
    }

    @Test
    @DisplayName("addUserGroup - userGroup이 들어오면 list에 추가")
    public void addUserGroupTest() {
        UserGroup userGroup = UserGroup.builder()
                .id(1L)
                .user(new User())
                .group(group)
                .color("test")
                .build();
        group.addUserGroup(userGroup);

        assertThat(group.getUserGroupList()).isNotEmpty();
        assertThat(group.getUserGroupList().size()).isEqualTo(1);
    }

    @Test
    @DisplayName("existUser - 인자로 들어온 유저가 존재하면 true 리턴")
    public void existUserReturnTrue() throws Exception {
        //given
        User user = User.builder()
                .id(1L)
                .userKey("test")
                .role("test")
                .build();

        UserGroup userGroup = UserGroup.builder()
                .id(1L)
                .user(user)
                .group(group)
                .color("test")
                .build();

        //then
        assertThat(group.existUser(user)).isFalse();
    }

    @Test
    @DisplayName("existUser - 인자로 들어온 유저가 존재하지 않으면 false 리턴")
    public void existUserReturnFalse() throws Exception {
        //given
        User user = User.builder()
                .id(1L)
                .userKey("test")
                .role("test")
                .build();

        UserGroup userGroup = UserGroup.builder()
                .id(1L)
                .user(user)
                .group(group)
                .color("test")
                .build();

        group.addUserGroup(userGroup);

        //then
        assertThat(group.existUser(user)).isTrue();
    }

    @Test
    @DisplayName("existUserById - 인자로 들어온 유저 id가 존재하면 true 리턴")
    public void existUserIdReturnTrue() throws Exception {
        //given
        User user = User.builder()
                .id(1L)
                .userKey("test")
                .role("test")
                .build();

        UserGroup userGroup = UserGroup.builder()
                .id(1L)
                .user(user)
                .group(group)
                .color("test")
                .build();

        //then
        assertThat(group.existUserById(user.getId())).isFalse();
    }

    @Test
    @DisplayName("existUserById - 인자로 들어온 유저id가 존재하지 않으면 false 리턴")
    public void existUserByIdReturnFalse() throws Exception {
        //given
        User user = User.builder()
                .id(1L)
                .userKey("test")
                .role("test")
                .build();

        UserGroup userGroup = UserGroup.builder()
                .id(1L)
                .user(user)
                .group(group)
                .color("test")
                .build();

        group.addUserGroup(userGroup);

        //then
        assertThat(group.existUserById(user.getId())).isTrue();
    }

    @Test
    @DisplayName("getUserNumInGroup - 그룹에 있는 유저가 없는 경우 유저의 수는 0 ")
    public void getUserNumInEmptyGroup() throws Exception {
        assertThat(group.getUserNumInGroup()).isEqualTo(0);
    }

    @Test
    @DisplayName("getUserNumInGroup - 그룹에 있는 유저가 있는 경우 유저의 수는 userGroup의 개수")
    public void getUserNumInGroup() throws Exception {
        int count = 5;
        for (int i = 0; i < count; i++) {
            group.addUserGroup(new UserGroup());
        }

        assertThat(group.getUserNumInGroup()).isEqualTo(count);
    }
}
