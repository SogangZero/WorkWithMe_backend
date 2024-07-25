package com.wwme.wwme.group.service;


import com.wwme.wwme.group.domain.Group;
import com.wwme.wwme.group.domain.GroupInvitation;
import com.wwme.wwme.group.domain.UserGroup;
import com.wwme.wwme.group.repository.GroupInvitationRepository;
import com.wwme.wwme.group.repository.GroupRepository;
import com.wwme.wwme.group.repository.UserGroupRepository;
import com.wwme.wwme.task.domain.DTO.sendDTO.TaskListReadByGroupSendDTO;
import com.wwme.wwme.task.domain.UserTask;
import com.wwme.wwme.task.repository.TagRepository;
import com.wwme.wwme.task.repository.TaskRepository;
import com.wwme.wwme.task.repository.UserTaskRepository;
import com.wwme.wwme.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class GroupServiceImpl implements GroupService {
    private final GroupRepository groupRepository;
    private final UserGroupRepository userGroupRepository;
    private final GroupInvitationRepository groupInvitationRepository;
    private final TagRepository tagRepository;
    private final TaskRepository taskRepository;

    @Override
    public Group createGroupWithUserAndColor(String groupName, User user, Long color) {
        if (groupName.length() > 20) {
            throw new IllegalArgumentException("Nickname should not exceed 20 characters!");
        }
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

    @Override
    public Group updateGroupNameAndColor(long groupId, String groupName, Long color, User user) {
        if (groupName.length() > 20) {
            throw new IllegalArgumentException("Nickname should not exceed 20 characters");
        }
        // Get Group and UserGroup from Database
        Optional<Group> groupOptional = groupRepository.findById(groupId);
        Group group = groupOptional.orElseThrow();
        Optional<UserGroup> userGroupOptional = userGroupRepository.findByUserAndGroup(user, group);
        UserGroup userGroup = userGroupOptional.orElseThrow();

        // Update groupName and Color
        group.setGroupName(groupName);
        userGroup.setColor(color);

        // Save the updates
        group = groupRepository.save(group);
        userGroupRepository.save(userGroup);

        return group;
    }

    @Override
    public List<User> getAllUserFromGroupId(long groupId) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new NoSuchElementException("Couldn't find Group with given groupId"));
        return getAllUserFromGroup(group);
    }

    @Override
    public List<User> getAllUserFromGroup(Group group) {
        Collection<UserGroup> userGroups = userGroupRepository.findByGroup(group);
        return userGroups
                .stream()
                .map(UserGroup::getUser)
                .collect(Collectors.toList());
    }

    @Override
    public String getGroupCode(long groupId) {
        GroupInvitation groupInvitation = groupInvitationRepository.findByGroupId(groupId)
                .orElseThrow(() -> new NoSuchElementException("Couldn't find GroupInvitation associated with given groupId"));
        return groupInvitation.getCode();
    }

    @Override
    public Group getGroupByCode(String groupCode) {
        GroupInvitation groupInvitation = groupInvitationRepository.findByCode(groupCode)
                .orElseThrow(() -> new NoSuchElementException("Couldn't find GroupInvitation with given GroupCode"));
        return groupInvitation.getGroup();
    }

    @Override
    public void deleteGroup(Group group) {
        userGroupRepository.deleteByGroup(group);
        groupInvitationRepository.deleteByGroup(group);
        tagRepository.deleteByGroup(group);
        taskRepository.deleteByGroup(group);
        groupRepository.delete(group);
    }

    @Override
    public Group getGroupByID(Long groupId) {
        return groupRepository.findById(groupId).orElseThrow(()->
                new NoSuchElementException("Could not find Group with given group ID : " + groupId + "In " +
                        "Function getGroupByID"));
    }

}
