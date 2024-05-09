package com.wwme.wwme.group.service;


import com.wwme.wwme.group.domain.Group;
import com.wwme.wwme.group.domain.UserGroup;
import com.wwme.wwme.group.repository.GroupRepository;
import com.wwme.wwme.group.repository.UserGroupRepository;
import com.wwme.wwme.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GroupServiceImpl implements GroupService {
    private final GroupRepository groupRepository;
    private final UserGroupRepository userGroupRepository;

    @Override
    public Group createGroupWithUserAndColor(String groupName, User user, String color) {
        Group newGroup = createAndSaveGroup(groupName);
        createAndSaveUserGroup(newGroup, user, color);
        return newGroup;
    }

    private Group createAndSaveGroup(String groupName) {
        Group newGroup = new Group();
        newGroup.setGroupName(groupName);
        return groupRepository.save(newGroup);
    }

    private UserGroup createAndSaveUserGroup(Group group, User user, String color) {
        UserGroup userGroup = new UserGroup();
        userGroup.setUser(user);
        userGroup.setGroup(group);
        userGroup.setColor(color);
        return userGroupRepository.save(userGroup);
    }
}
