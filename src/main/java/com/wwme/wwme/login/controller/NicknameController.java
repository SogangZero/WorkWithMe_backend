package com.wwme.wwme.login.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wwme.wwme.login.aop.Login;
import com.wwme.wwme.login.domain.dto.DataDTO;
import com.wwme.wwme.login.domain.dto.ErrorDTO;
import com.wwme.wwme.login.domain.dto.NicknameDTO;
import com.wwme.wwme.login.domain.dto.SuccessDTO;
import com.wwme.wwme.login.exception.JwtTokenException;
import com.wwme.wwme.login.jwt.JWTUtil;
import com.wwme.wwme.login.service.NicknameService;
import com.wwme.wwme.user.domain.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.NoSuchElementException;

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
            String userKey = user.getUserKey();
            nicknameService.saveNickname(nickname, userKey);
            log.info("User[{}] request nickname setting by {}", userKey, nickname);

            SuccessDTO successDTO = new SuccessDTO(true);
            return new ResponseEntity<>(new DataDTO(successDTO), HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            ErrorDTO errorDTO = new ErrorDTO(e.getMessage());
            return new ResponseEntity<>(errorDTO, HttpStatus.BAD_REQUEST);
        }

    }
}
