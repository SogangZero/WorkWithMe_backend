package com.wwme.wwme.group.service;

import com.wwme.wwme.group.domain.Group;
import com.wwme.wwme.group.domain.UserGroup;
import com.wwme.wwme.group.repository.GroupRepository;
import com.wwme.wwme.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class UserGroupServiceImpl implements UserGroupService{
    private final GroupRepository groupRepository;

    /**
     * Returns UserGroup associated with given group id and user
     * @param groupId id of group associated with UserGroup
     * @param user User associated with UserGroup
     * @return UserGroup that is found
     * @throws NoSuchElementException group id is invalid
     * @throws IllegalAccessException group doesn't contain the user
     */
    @Override
    public UserGroup getUserGroupByIdAndUser(long groupId, User user) throws NoSuchElementException, IllegalAccessException {
        Group group = groupRepository.findById(groupId).orElseThrow();

        // if matching user is inside found group return userGroup
        return group.getUserGroupList().stream()
                .filter(userGroup -> userGroup.getUser().equals(user))
                .findFirst()
                .orElseThrow();
    }
}
