package com.wwme.wwme.group.controller;

import com.wwme.wwme.group.DTO.*;
import com.wwme.wwme.group.domain.Group;
import com.wwme.wwme.group.domain.UserGroup;
import com.wwme.wwme.group.service.GroupService;
import com.wwme.wwme.group.service.UserGroupService;
import com.wwme.wwme.user.domain.User;
import com.wwme.wwme.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.List;

@RestController
@RequestMapping("/group")
@RequiredArgsConstructor
public class GroupController {
    private final GroupService groupService;
    private final UserService userService;
    private final UserGroupService userGroupService;

    // Create group
    @PostMapping
    public ResponseEntity<?> groupCreate(
            @RequestBody GroupCreateRequestDTO requestDTO,
            @CookieValue(value = "Authorization") String jwtString
    ) {
        try {
            // Get current user from jwt
            User user = userService.getUserFromJWTString(jwtString);

            // Create group based on parameters
            Group createdGroup = groupService.createGroupWithUserAndColor(
                    requestDTO.getGroupName(),
                    user,
                    requestDTO.getGroupColor()
            );

            // Formulate response
            GroupCreateSuccessResponseDTO responseDTO = new GroupCreateSuccessResponseDTO();
            responseDTO.setGroupId(createdGroup.getId());
            responseDTO.setSuccess(true);
            responseDTO.setGroupInvitationLink("temptemp"); // TODO : GROUP INVITATION LINK
            return new ResponseEntity<>(responseDTO, HttpStatus.OK);
        } catch (Exception e) {
            GroupCreateFailResponseDTO responseDTO = new GroupCreateFailResponseDTO();
            responseDTO.setSuccess(false);
            return new ResponseEntity<>(responseDTO, HttpStatus.BAD_REQUEST);
        }
    }

    // Update Group
    @PutMapping
    public ResponseEntity<?> groupUpdate(
            @RequestBody GroupUpdateRequestDTO requestDTO,
            @CookieValue("Authorization") String jwtString
    ) {
        try {
            // Get user from JWT
            User user = userService.getUserFromJWTString(jwtString);

            // Update group with parameters
            Group updatedGroup = groupService.updateGroupNameAndColor(
                    requestDTO.getGroupId(),
                    requestDTO.getGroupName(),
                    requestDTO.getGroupColor(),
                    user
            );

            // Formulate response
            GroupUpdateSuccessResponseDTO responseDTO = new GroupUpdateSuccessResponseDTO();
            responseDTO.setSuccess(true);
            responseDTO.setGroupId(updatedGroup.getId());

            return new ResponseEntity<>(responseDTO, HttpStatus.OK);
        } catch (Exception e) {
            GroupUpdateFailResponseDTO responseDTO = new GroupUpdateFailResponseDTO();
            responseDTO.setSuccess(false);

            return new ResponseEntity<>(responseDTO, HttpStatus.OK);
        }
    }

    // Get group information based on group id
    @GetMapping
    public ResponseEntity<?> groupRead(
            @RequestBody GroupReadRequestDTO requestDTO,
            @CookieValue("Authorization") String jwtString
    ) {
        try {
            // Get user from JWT
            User user = userService.getUserFromJWTString(jwtString);

            // Get UserGroup and Group from group id and user
            UserGroup userGroup = userGroupService.getUserGroupByIdAndUser(requestDTO.getGroupId(), user);
            Group group = userGroup.getGroup();

            // Formulate Response
            GroupReadSuccessResponseDTO responseDTO = new GroupReadSuccessResponseDTO();
            responseDTO.setSuccess(true);
            responseDTO.setGroupName(group.getGroupName());
            responseDTO.setGroupColor(userGroup.getColor());
            for (UserGroup curUserGroup : group.getUserGroupList()) {
                GroupReadSuccessResponseDTO.UserDTO userDTO = new GroupReadSuccessResponseDTO.UserDTO();
                userDTO.setUserId(curUserGroup.getUser().getId());
                responseDTO.addUserDTOToList(userDTO);
            }

            return new ResponseEntity<>(responseDTO, HttpStatus.OK);
        } catch (Exception e) {
            GroupReadFailResponseDTO responseDTO = new GroupReadFailResponseDTO();
            responseDTO.setSuccess(false);
            return new ResponseEntity<>(responseDTO, HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/all")
    public ResponseEntity<?> getAllGroupOfUser(
            @CookieValue("Authorization") String jwtString
    ){
        try {
            // Get user from JWT
            User user = userService.getUserFromJWTString(jwtString);

            // Get all groups from user
            Collection<UserGroup> userGroups = userGroupService.getAllUserGroupOfUser(user);

            // Formulate Response
            GroupReadAllSuccessResponseDTO responseDTO = new GroupReadAllSuccessResponseDTO();
            responseDTO.setSuccess(true);
            List<GroupReadAllSuccessResponseDTO.GroupDTO> groupDTOList = userGroups.stream()
                    .map(userGroup -> {
                        GroupReadAllSuccessResponseDTO.GroupDTO groupDTO =
                                new GroupReadAllSuccessResponseDTO.GroupDTO();
                        Group group = userGroup.getGroup();
                        groupDTO.setGroupId(group.getId());
                        groupDTO.setGroupName(group.getGroupName());
                        groupDTO.setGroupColor(userGroup.getColor());
                        return groupDTO;
                    })
                    .toList();
            responseDTO.setGroupDTOList(groupDTOList);

            return new ResponseEntity<>(responseDTO, HttpStatus.OK);
        }
        catch (Exception e) {
            GroupReadAllFailResponseDTO responseDTO = new GroupReadAllFailResponseDTO();
            responseDTO.setSuccess(false);
            return new ResponseEntity<>(responseDTO, HttpStatus.BAD_REQUEST);
        }
    }
}
