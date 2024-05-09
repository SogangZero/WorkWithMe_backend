package com.wwme.wwme.group.controller;

import com.wwme.wwme.group.DTO.GroupCreateFailResponseDTO;
import com.wwme.wwme.group.DTO.GroupCreateRequestDTO;
import com.wwme.wwme.group.DTO.GroupCreateSuccessResponseDTO;
import com.wwme.wwme.group.domain.Group;
import com.wwme.wwme.group.service.GroupService;
import com.wwme.wwme.login.jwt.JWTUtil;
import com.wwme.wwme.user.domain.User;
import com.wwme.wwme.user.repository.UserRepository;
import com.wwme.wwme.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequiredArgsConstructor
public class GroupController {
    private final GroupService groupService;
    private final UserService userService;

    // Create and Update group
    @PostMapping("/group")
    public ResponseEntity<?> groupCreateUpdate(
            @RequestBody GroupCreateRequestDTO requestDTO,
            @CookieValue(value="Authorization") String jwtString
    ) {
        try {
            User user = userService.getUserFromJWTString(jwtString);
            Group createdGroup = groupService.createGroupWithUserAndColor(
                    requestDTO.getGroupName(),
                    user,
                    requestDTO.getGroupColor()
            );

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
}
