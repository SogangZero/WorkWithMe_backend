package com.wwme.wwme.group.controller;

import com.wwme.wwme.group.DTO.*;
import com.wwme.wwme.group.domain.Group;
import com.wwme.wwme.group.domain.UserGroup;
import com.wwme.wwme.group.service.GroupInvitationService;
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
    private final GroupInvitationService groupInvitationService;

    /**
     * POST /group <br>
     * Creates a group, and returns the created group information <br>
     *
     * @param requestDTO DTO for request body json
     * @param jwtString  JWT
     * @return
     */
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

            // Create group
            String code = groupInvitationService.createGroupInvitation(createdGroup);

            // Formulate response
            GroupCreateSuccessResponseDTO responseDTO = new GroupCreateSuccessResponseDTO();
            responseDTO.setGroupId(createdGroup.getId());
            responseDTO.setSuccess(true);
            responseDTO.setGroupInvitationLink("/group/invitation/" + code);
            return new ResponseEntity<>(responseDTO, HttpStatus.OK);
        } catch (Exception e) {
            GroupCreateFailResponseDTO responseDTO = new GroupCreateFailResponseDTO();
            responseDTO.setSuccess(false);
            return new ResponseEntity<>(responseDTO, HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * PUT /group <br>
     * Updates the group name and color of given group id <br>
     * User has to be inside the group
     *
     * @param requestDTO DTO for body json
     * @param jwtString  JWT
     * @return
     */
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

            return new ResponseEntity<>(responseDTO, HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * GET /group <br>
     * Returns one group information with the given group id <br>
     * User has to be inside the group
     *
     * @param requestDTO request body json
     * @param jwtString  JWT
     * @return
     */
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

    /**
     * GET /group/all <br>
     * Returns all group information of current user defined by JWT
     *
     * @param jwtString JWT
     * @return
     */
    @GetMapping("/all")
    public ResponseEntity<?> groupReadAllOfUser(
            @CookieValue("Authorization") String jwtString
    ) {
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
        } catch (Exception e) {
            GroupReadAllFailResponseDTO responseDTO = new GroupReadAllFailResponseDTO();
            responseDTO.setSuccess(false);
            return new ResponseEntity<>(responseDTO, HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/invitation/accept")
    public ResponseEntity<?> groupInvitationAccept(
            @RequestBody InvitationAcceptRequestDTO requestDTO,
            @CookieValue("Authorization") String jwtString
    ) {
        try {
            User user = userService.getUserFromJWTString(jwtString);
            Group group = groupInvitationService.acceptInvitation(
                    requestDTO.getCode(),
                    user,
                    requestDTO.getGroupColor()
            );

            var responseDTO = new InvitationAcceptResponseSuccessDTO();
            responseDTO.setSuccess(true);
            responseDTO.setGroupId(group.getId());
            return new ResponseEntity<>(responseDTO, HttpStatus.OK);
        } catch (Exception e) {
            var responseDTO = new InvitationAcceptResponseFailDTO();
            responseDTO.setSuccess(false);

            return new ResponseEntity<>(responseDTO, HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping
    public ResponseEntity<?> groupLeave(
            @RequestBody GroupLeaveRequestDTO requestDTO,
            @CookieValue("Authorization") String jwtString
    ) {
        try {
            User user = userService.getUserFromJWTString(jwtString);

            userGroupService.removeUserFromGroup(requestDTO.getGroupId(), user);

            var responseDTO = new GroupLeaveResponseDTO();
            responseDTO.setSuccess(true);
            return new ResponseEntity<>(responseDTO, HttpStatus.OK);
        } catch (Exception e) {
            var responseDTO = new GroupLeaveResponseDTO();
            responseDTO.setSuccess(false);

            return new ResponseEntity<>(responseDTO, HttpStatus.BAD_REQUEST);
        }
    }
}
