package com.wwme.wwme.group.controller;

import com.wwme.wwme.group.DTO.*;
import com.wwme.wwme.group.domain.Group;
import com.wwme.wwme.group.service.GroupService;
import com.wwme.wwme.user.domain.User;
import com.wwme.wwme.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/group")
@RequiredArgsConstructor
public class GroupController {
    private final GroupService groupService;
    private final UserService userService;

    // Create group
    @PostMapping
    public ResponseEntity<?> groupCreate(
            @RequestBody GroupCreateRequestDTO requestDTO,
            @CookieValue(value="Authorization") String jwtString
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

            // Return response
            GroupCreateSuccessResponseDTO responseDTO = new GroupCreateSuccessResponseDTO();
            responseDTO.setGroupId(createdGroup.getId());
            responseDTO.setSuccess(true);
            responseDTO.setGroupInvitationLink("temptemp"); // TODO : GROUP INVITATION LINK
            return new ResponseEntity<>(responseDTO, HttpStatus.OK);
        }
        catch(Exception e) {
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
    ){
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

            // Return response
            GroupUpdateSuccessDTO responseDTO = new GroupUpdateSuccessDTO();
            responseDTO.setSuccess(true);
            responseDTO.setGroupId(updatedGroup.getId());

            return new ResponseEntity<>(responseDTO, HttpStatus.OK);
        } catch (Exception e) {
            GroupUpdateFailDTO responseDTO = new GroupUpdateFailDTO();
            responseDTO.setSuccess(false);

            return new ResponseEntity<>(responseDTO, HttpStatus.OK);
        }
    }

    // Get group information based on group id
    @GetMapping
    public ResponseEntity<?> groupRead(
            @RequestBody GroupUpdateRequestDTO requestDTO,
            @CookieValue("Authorization") String jwtString
    ) {

    }

}
