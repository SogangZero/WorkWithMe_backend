package com.wwme.wwme.group.service;

import com.wwme.wwme.group.domain.Group;
import com.wwme.wwme.group.domain.UserGroup;
import com.wwme.wwme.group.repository.GroupRepository;
import com.wwme.wwme.group.repository.UserGroupRepository;
import com.wwme.wwme.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
@Transactional
public class UserGroupServiceImpl implements UserGroupService {
    private final GroupRepository groupRepository;
    private final UserGroupRepository userGroupRepository;

    @Override
    public UserGroup getUserGroupByIdAndUser(long groupId, User user) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new NoSuchElementException("Couldn't find Group with given groupId"));

        // return if current user is inside found group
        // throw exception if not
        return group.getUserGroupList().stream()
                .filter(userGroup -> userGroup.getUser().equals(user))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("Current user isn't found in the group of given group id"));
    }

    @Override
    public Collection<UserGroup> getAllUserGroupOfUser(User user) {
        return userGroupRepository.findByUser(user);
    }

    @Override
    public UserGroup addUserToGroupWithColor(Group group, User user, Long color) {
        if (userGroupRepository.findByUserAndGroup(user, group).isPresent()) {
            throw new IllegalArgumentException("User is already in group");
        }

        UserGroup userGroup = new UserGroup();
        userGroup.setGroup(group);
        userGroup.setUser(user);
        userGroup.setColor(color);

        userGroup = userGroupRepository.save(userGroup);
        return userGroup;
    }

    @Override
    public void removeUserFromGroup(long groupId, User user) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new NoSuchElementException("Couldn't find Group with given groupId"));

        UserGroup userGroup = userGroupRepository.findByUserAndGroup(user, group)
                .orElseThrow(() -> new NoSuchElementException("Couldn't find UserGroup with given User and Group"));
        userGroupRepository.delete(userGroup);
    }
}
