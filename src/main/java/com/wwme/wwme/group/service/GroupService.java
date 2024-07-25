package com.wwme.wwme.group.service;

import com.wwme.wwme.group.domain.Group;
import com.wwme.wwme.user.domain.User;

import java.util.Collection;
import java.util.List;
import java.util.NoSuchElementException;

public interface GroupService {
    Group createGroupWithUserAndColor(String groupName, User user, Long color);

    Group updateGroupNameAndColor(long groupId, String groupName, Long color, User user);

    List<User> getAllUserFromGroupId(long groupId);
    List<User> getAllUserFromGroup(Group group);

    String getGroupCode(long groupId);

    Group getGroupByCode(String groupCode);

    void deleteGroup(Group group);

    Group getGroupByID(Long groupId);
}
