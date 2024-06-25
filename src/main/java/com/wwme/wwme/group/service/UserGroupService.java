package com.wwme.wwme.group.service;

import com.wwme.wwme.group.domain.Group;
import com.wwme.wwme.group.domain.UserGroup;
import com.wwme.wwme.user.domain.User;

import java.util.Collection;
import java.util.NoSuchElementException;

public interface UserGroupService {

    /**
     * Returns UserGroup associated with given group id and user
     * @param groupId id of group associated with UserGroup
     * @param user User associated with UserGroup
     * @return UserGroup that is found
     * @throws NoSuchElementException group id is invalid or group doesn't contain the user
     */
    UserGroup getUserGroupByIdAndUser(long groupId, User user) throws NoSuchElementException;

    /**
     * Finds all UserGroup of user. Order might not be preserved
     * @param user user to search for groups
     * @return Collection of UserGroup
     */
    Collection<UserGroup> getAllUserGroupOfUser(User user);

    UserGroup addUserToGroupWithColor(Group group, User user, Long color);

    void removeUserFromGroup(long groupId, User user);
}
