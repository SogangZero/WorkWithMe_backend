package com.wwme.wwme.group.service;

import com.wwme.wwme.group.domain.Group;
import com.wwme.wwme.user.domain.User;

public interface GroupService {
    Group createGroupWithUserAndColor(String groupName, User user, String color);
    Group updateGroupNameAndColor(long groupId, String groupName, String color, User user);
}
