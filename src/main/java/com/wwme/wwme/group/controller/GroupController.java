package com.wwme.wwme.group.controller;

import com.wwme.wwme.group.DTO.GroupCreateFailResponseDTO;
import com.wwme.wwme.group.DTO.GroupCreateRequestDTO;
import com.wwme.wwme.group.DTO.GroupCreateSuccessResponseDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GroupController {

    // Create and Update group
    @PostMapping("/group")
    public ResponseEntity<?> groupCreateUpdate(@RequestBody GroupCreateRequestDTO requestDTO) {
        try {


            GroupCreateSuccessResponseDTO responseDTO = new GroupCreateSuccessResponseDTO();
            responseDTO.setGroupId(0);
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
