package com.wwme.wwme.group.controller;

import com.wwme.wwme.group.DTO.*;
import com.wwme.wwme.group.domain.Group;
import com.wwme.wwme.group.domain.UserGroup;
import com.wwme.wwme.group.service.GroupDtoConverter;
import com.wwme.wwme.group.service.GroupInvitationService;
import com.wwme.wwme.group.service.GroupService;
import com.wwme.wwme.group.service.UserGroupService;
import com.wwme.wwme.login.aop.Login;
import com.wwme.wwme.notification.service.NotificationSender;
import com.wwme.wwme.user.domain.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.List;

@RestController
@RequestMapping("/group")
@RequiredArgsConstructor
@Slf4j
public class GroupController {
    private final GroupService groupService;
    private final UserGroupService userGroupService;
    private final GroupInvitationService groupInvitationService;
    private final GroupDtoConverter converter;
    private final NotificationSender notificationSender;

    /**
     * POST /group <br>
     * Creates a group, and returns the created group information <br>
     *
     * @param requestDTO DTO for request body json
     * @return
     */
    @PostMapping
    public ResponseEntity<?> groupCreate(
            @RequestBody GroupCreateRequestDTO requestDTO,
            @Login User user
    ) {
        try {
            // Create group based on parameters
            Group createdGroup = groupService.createGroupWithUserAndColor(
                    requestDTO.getGroupName(),
                    user,
                    requestDTO.getGroupColor()
            );

            // Create group
            String code = groupInvitationService.createGroupInvitation(createdGroup);

            // Formulate response
            var responseDTO = new GroupCreateSuccessResponseDTO(
                    createdGroup.getId(), code,
                    requestDTO.getGroupName(), requestDTO.getGroupColor());
            return new ResponseEntity<>(new DataWrapDTO(responseDTO), HttpStatus.OK);
        } catch (Exception e) {
            log.error("Input: {}", requestDTO, e);
            var responseDTO = new ErrorWrapDTO(e.getMessage());
            return new ResponseEntity<>(responseDTO, HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * PUT /group <br>
     * Updates the group name and color of given group id <br>
     * User has to be inside the group
     *
     * @param requestDTO DTO for request
     * @return
     */
    @PutMapping
    public ResponseEntity<?> groupUpdate(
            @RequestBody GroupUpdateRequestDTO requestDTO,
            @Login User user
    ) {
        try {
            // Update group with parameters
            Group updatedGroup = groupService.updateGroupNameAndColor(
                    requestDTO.getGroupId(),
                    requestDTO.getGroupName(),
                    requestDTO.getGroupColor(),
                    user
            );

            // Formulate response
            GroupUpdateSuccessResponseDTO responseDTO = new GroupUpdateSuccessResponseDTO();
            responseDTO.setGroupId(updatedGroup.getId());

            return new ResponseEntity<>(new DataWrapDTO(responseDTO), HttpStatus.OK);
        } catch (Exception e) {
            log.error("Input: {}", requestDTO, e);
            var responseDTO = new ErrorWrapDTO(e.getMessage());
            return new ResponseEntity<>(responseDTO, HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * GET /group <br>
     * Returns one group information with the given group id <br>
     * User has to be inside the group
     *
     * @param groupId   group id that is read
     * @return
     */
    @GetMapping
    public ResponseEntity<?> groupRead(
            @RequestParam("group_id") long groupId,
            @Login User user
    ) {
        try {
            // Get UserGroup and Group from group id and user
            UserGroup userGroup = userGroupService.getUserGroupByIdAndUser(groupId, user);

            // Formulate Response
            var responseDTO = converter.convertToGroupReadDTO(userGroup);
            return new ResponseEntity<>(new DataWrapDTO(responseDTO), HttpStatus.OK);
        } catch (Exception e) {
            log.error("Input: groupId: {}", groupId, e);
            var responseDTO = new ErrorWrapDTO(e.getMessage());
            return new ResponseEntity<>(responseDTO, HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/user/all")
    public ResponseEntity<?> groupUserListRead(
            @RequestParam(name = "group_id") long groupId
    ) {
        try {
            List<User> users = groupService.getAllUserFromGroupId(groupId);
            String groupCode = groupService.getGroupCode(groupId);

            // formulate response
            var responseDTO = converter.convertToGroupUserListReadDTO(users, groupCode);
            return new ResponseEntity<>(new DataWrapDTO(responseDTO), HttpStatus.OK);
        } catch (Exception e) {
            log.error("Input: groupId: {}", groupId, e);
            var responseDTO = new ErrorWrapDTO(e.getMessage());
            return new ResponseEntity<>(responseDTO, HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * GET /group/all <br>
     * Returns all group information of current user defined by JWT
     *
     * @return
     */
    @GetMapping("/all")
    public ResponseEntity<?> groupReadAllOfUser(
            @Login User user
    ) {
        try {
            // Get all groups from user
            Collection<UserGroup> userGroups = userGroupService.getAllUserGroupOfUser(user);

            // Formulate Response
            var responseDTO = converter.convertToGroupReadAllDTO(userGroups);

            return new ResponseEntity<>(responseDTO, HttpStatus.OK);
        } catch (Exception e) {
            log.error("User: {}", user, e);
            var responseDTO = new ErrorWrapDTO(e.getMessage());
            return new ResponseEntity<>(responseDTO, HttpStatus.BAD_REQUEST);
        }
    }


    @GetMapping("/code")
    public ResponseEntity<?> groupReadWithCode(
            @RequestParam("group_code") String groupCode
    ) {
        try {
            Group group = groupService.getGroupByCode(groupCode);
            List<User> users = groupService.getAllUserFromGroup(group);

            // formulate response
            var responseDTO = converter.convertToGroupReadWithCodeDTO(group, users);
            return new ResponseEntity<>(new DataWrapDTO(responseDTO), HttpStatus.OK);
        } catch (Exception e) {
            log.error("Input: groupCode: {}", groupCode, e);
            var responseDTO = new ErrorWrapDTO(e.getMessage());
            return new ResponseEntity<>(responseDTO, HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/invitation/accept")
    public ResponseEntity<?> groupInvitationAccept(
            @RequestBody InvitationAcceptRequestDTO requestDTO,
            @Login User user
    ) {
        try {
            Group group = groupInvitationService.acceptInvitation(
                    requestDTO.getGroupCode(),
                    user,
                    requestDTO.getGroupColor()
            );
            notificationSender.sendOnGroupEntranceNotification(group, group.getUserGroupList());

            var responseDTO = new InvitationAcceptResponseSuccessDTO();
            responseDTO.setGroupId(group.getId());
            return new ResponseEntity<>(new DataWrapDTO(responseDTO), HttpStatus.OK);
        } catch (Exception e) {
            log.error("Input: {}", requestDTO, e);
            var responseDTO = new ErrorWrapDTO(e.getMessage());
            return new ResponseEntity<>(responseDTO, HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping
    public ResponseEntity<?> groupLeave(
            @RequestParam("group_id") long groupId,
            @Login User user
    ) {
        try {
            userGroupService.removeUserFromGroup(groupId, user);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            log.error("Input: groupId: {}", groupId, e);
            var responseDTO = new ErrorWrapDTO(e.getMessage());
            return new ResponseEntity<>(responseDTO, HttpStatus.BAD_REQUEST);
        }
    }
}
