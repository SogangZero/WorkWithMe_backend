package com.wwme.wwme.group.service;

import com.wwme.wwme.group.domain.Group;
import com.wwme.wwme.user.domain.User;

import java.util.Collection;
import java.util.List;
import java.util.NoSuchElementException;

public interface GroupService {
    /**
     * Creates group given group name, user, and color
     * @param groupName group name of created group
     * @param user the user creating the group
     * @param color color associated with the user and group
     * @return group that is created
     */
    Group createGroupWithUserAndColor(String groupName, User user, String color);

    /**
     * Updates Group with given group name and color
     * @param groupId id of group
     * @param groupName new group name
     * @param color new color
     * @param user user associated with the group being updated (needed for updating color)
     * @throws java.util.NoSuchElementException
     *              Group is missing or UserGroup is missing
     * @return updated group
     */
    Group updateGroupNameAndColor(long groupId, String groupName, String color, User user) throws NoSuchElementException;

    List<User> getAllUserFromGroupId(long groupId);
    List<User> getAllUserFromGroup(Group group);

    String getGroupCode(long groupId);

    Group getGroupByCode(String groupCode);

}
