package com.wwme.wwme.group.controller;

import com.wwme.wwme.group.DTO.GroupCreateFailResponseDTO;
import com.wwme.wwme.group.DTO.GroupCreateRequestDTO;
import com.wwme.wwme.group.DTO.GroupCreateSuccessResponseDTO;
import com.wwme.wwme.group.domain.Group;
import com.wwme.wwme.group.service.GroupService;
import com.wwme.wwme.login.jwt.JWTUtil;
import com.wwme.wwme.user.domain.User;
import com.wwme.wwme.user.repository.UserRepository;
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
    private final UserRepository userRepository;
    private final JWTUtil jwtUtil;

    private User getUserFromJWTString(String jwtString) {
        String userKey = jwtUtil.getUserKey(jwtString);
        Optional<User> optionalUserKey = userRepository.findByUserKey(userKey);
        if(optionalUserKey.isEmpty()) {
            return null;
        }
        return optionalUserKey.get();
    }

    // Create and Update group
    @PostMapping("/group")
    public ResponseEntity<?> groupCreateUpdate(
            @RequestBody GroupCreateRequestDTO requestDTO,
            @CookieValue(value="Authorization") String jwtString
    ) {
        try {
            User user = getUserFromJWTString(jwtString);
            Group createdGroup = groupService.createGroupWithUserAndColor(
                    requestDTO.getGroupName(),
                    user,
                    requestDTO.getGroupColor()
            );

            GroupCreateSuccessResponseDTO responseDTO = new GroupCreateSuccessResponseDTO();
            responseDTO.setGroupId(createdGroup.getId());
            responseDTO.setSuccess(true);
            responseDTO.setGroupInvitationLink("temptemp");
            return new ResponseEntity<>(responseDTO, HttpStatus.OK);
        }
        catch(Exception e) {
            GroupCreateFailResponseDTO responseDTO = new GroupCreateFailResponseDTO();
            return new ResponseEntity<>(responseDTO, HttpStatus.BAD_REQUEST);
        }
    }

}
