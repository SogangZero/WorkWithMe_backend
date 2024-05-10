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
        Group newGroup = new Group();
        newGroup.setGroupName(groupName);
        newGroup = groupRepository.save(newGroup);

        UserGroup userGroup = new UserGroup();
        userGroup.setUser(user);
        userGroup.setGroup(newGroup);
        userGroup.setColor(color);
        userGroupRepository.save(userGroup);

        newGroup.getUserGroup().add(userGroup);

        return newGroup;
    }
}
