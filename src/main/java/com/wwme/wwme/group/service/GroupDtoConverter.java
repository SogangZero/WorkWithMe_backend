package com.wwme.wwme.group.service;

import com.wwme.wwme.group.DTO.GroupReadAllSuccessResponseDTO;
import com.wwme.wwme.group.DTO.GroupReadSuccessResponseDTO;
import com.wwme.wwme.group.DTO.GroupReadWithCodeSuccessResponseDTO;
import com.wwme.wwme.group.DTO.GroupUserListReadResponseSuccessDTO;
import com.wwme.wwme.group.domain.Group;
import com.wwme.wwme.group.domain.UserGroup;
import com.wwme.wwme.user.domain.User;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class GroupDtoConverter {

    public GroupReadSuccessResponseDTO convertToGroupReadDTO(UserGroup userGroup) {
        var group = userGroup.getGroup();
        var userDtoList = group.getUserGroupList().stream()
                .map((curUserGroup) ->
                        new GroupReadSuccessResponseDTO.UserDTO(curUserGroup.getUser().getId()))
                .toList();

        return new GroupReadSuccessResponseDTO(
                group.getGroupName(),
                userGroup.getColor(),
                userDtoList
        );
    }

    public GroupUserListReadResponseSuccessDTO convertToGroupUserListReadDTO(List<User> users, String groupCode) {
        var responseUsers = users.stream().map((user) -> {
            var curUser = new GroupUserListReadResponseSuccessDTO.User();
            curUser.setNickname(user.getNickname());
            curUser.setProfileImageId(0);
            return curUser;
        }).collect(Collectors.toList());

        return new GroupUserListReadResponseSuccessDTO(responseUsers, groupCode);
    }

    public GroupReadAllSuccessResponseDTO convertToGroupReadAllDTO(Collection<UserGroup> userGroups) {
        List<GroupReadAllSuccessResponseDTO.GroupDTO> groupDTOList = userGroups.stream()
                .map(userGroup -> {
                    GroupReadAllSuccessResponseDTO.GroupDTO groupDTO =
                            new GroupReadAllSuccessResponseDTO.GroupDTO();
                    Group group = userGroup.getGroup();
                    groupDTO.setGroupId(group.getId());
                    groupDTO.setGroupName(group.getGroupName());
                    groupDTO.setGroupColor(userGroup.getColor());
                    groupDTO.setNumPeople(2);
                    return groupDTO;
                })
                .toList();

        return new GroupReadAllSuccessResponseDTO(groupDTOList);
    }

    public GroupReadWithCodeSuccessResponseDTO convertToGroupReadWithCodeDTO(Group group, List<User> users) {
        var responseUsers = users
                .stream()
                .map((user) -> {
                    var curUser = new GroupReadWithCodeSuccessResponseDTO.User();
                    curUser.setUserId(user.getId());
                    curUser.setNickname(user.getNickname());
                    return curUser;
                })
                .toList();

        return new GroupReadWithCodeSuccessResponseDTO(
                group.getGroupName(),
                responseUsers
        );
    }
}
