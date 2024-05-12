package com.wwme.wwme.group.service;

import com.wwme.wwme.group.domain.Group;
import com.wwme.wwme.group.domain.UserGroup;
import com.wwme.wwme.group.repository.GroupRepository;
import com.wwme.wwme.group.repository.UserGroupRepository;
import com.wwme.wwme.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class UserGroupServiceImpl implements UserGroupService{
    private final GroupRepository groupRepository;
    private final UserGroupRepository userGroupRepository;

    @Override
    public UserGroup getUserGroupByIdAndUser(long groupId, User user) throws NoSuchElementException, IllegalAccessException {
        Group group = groupRepository.findById(groupId).orElseThrow();

        // if matching user is inside found group return userGroup
        return group.getUserGroupList().stream()
                .filter(userGroup -> userGroup.getUser().equals(user))
                .findFirst()
                .orElseThrow();
    }

    @Override
    public Collection<UserGroup> getAllUserGroupOfUser(User user) {
        return userGroupRepository.findByUser(user);
    }
}
