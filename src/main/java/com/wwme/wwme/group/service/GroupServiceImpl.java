package com.wwme.wwme.group.service;


import com.wwme.wwme.group.domain.Group;
import com.wwme.wwme.group.domain.UserGroup;
import com.wwme.wwme.group.repository.GroupRepository;
import com.wwme.wwme.group.repository.UserGroupRepository;
import com.wwme.wwme.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class GroupServiceImpl implements GroupService {
    private final GroupRepository groupRepository;
    private final UserGroupRepository userGroupRepository;

    @Override
    public Group createGroupWithUserAndColor(String groupName, User user, String color) {
        // Create new Group
        Group newGroup = new Group();
        newGroup.setGroupName(groupName);
        newGroup = groupRepository.save(newGroup);

        // Create new UserGroup
        UserGroup userGroup = new UserGroup();
        userGroup.setUser(user);
        userGroup.setGroup(newGroup); // manages reverse relationship in set method
        userGroup.setColor(color);
        userGroupRepository.save(userGroup);

        return newGroup;
    }

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
    @Override
    public Group updateGroupNameAndColor(long groupId, String groupName, String color, User user) throws NoSuchElementException {
        // Get Group and UserGroup from Database
        Optional<Group> groupOptional = groupRepository.findById(groupId);
        Group group = groupOptional.orElseThrow(); // NoSuchElementException
        Optional<UserGroup> userGroupOptional = userGroupRepository.findByUserAndGroup(user, group);
        UserGroup userGroup = userGroupOptional.orElseThrow(); // NoSuchElementException

        // Update groupName and Color
        group.setGroupName(groupName);
        userGroup.setColor(color);

        // Save the updates
        group = groupRepository.save(group);
        userGroupRepository.save(userGroup);

        return group;
    }


}
