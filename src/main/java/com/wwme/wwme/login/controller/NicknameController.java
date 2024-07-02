package com.wwme.wwme.login.controller;

import com.wwme.wwme.login.aop.Login;
import com.wwme.wwme.login.domain.dto.response.DataDTO;
import com.wwme.wwme.login.domain.dto.response.ErrorDTO;
import com.wwme.wwme.login.domain.dto.NicknameDTO;
import com.wwme.wwme.login.domain.dto.response.SuccessDTO;
import com.wwme.wwme.login.service.NicknameService;
import com.wwme.wwme.user.domain.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
public class NicknameController {
    private final NicknameService nicknameService;

    @PostMapping("/login/nickname")
    public ResponseEntity<?> saveNickname(@RequestBody NicknameDTO nicknameDTO,
                                          @Login User user) {
        try {
            String nickname = nicknameDTO.getNickname();
            Long profileImage = nicknameDTO.getProfile_image_id();
            String userKey = user.getUserKey();
            nicknameService.saveNickname(nickname, userKey, profileImage);
            log.info("User[{}] request nickname setting by {}", userKey, nickname);

            SuccessDTO successDTO = new SuccessDTO(true);
            return new ResponseEntity<>(new DataDTO(successDTO), HttpStatus.OK);
        } catch (Exception e) {
            ErrorDTO errorDTO = new ErrorDTO(e.getMessage());
            return new ResponseEntity<>(errorDTO, HttpStatus.BAD_REQUEST);
        }
    }
}
