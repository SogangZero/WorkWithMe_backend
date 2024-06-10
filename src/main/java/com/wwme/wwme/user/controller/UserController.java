package com.wwme.wwme.user.controller;

import com.wwme.wwme.login.aop.Login;
import com.wwme.wwme.login.domain.dto.response.DataDTO;
import com.wwme.wwme.login.domain.dto.response.ErrorDTO;
import com.wwme.wwme.user.domain.User;
import com.wwme.wwme.user.domain.dto.UserInfoDTO;
import com.wwme.wwme.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/user")
public class UserController {
    private final UserService userService;

    @GetMapping
    public ResponseEntity<?> userInfo(@Login User user) {
        try {
            log.info("Request user info ID {}", user.getId());
            UserInfoDTO userInfo = userService.getUserInfo(user);

            return new ResponseEntity<>(new DataDTO(userInfo), HttpStatus.OK);
        } catch (Exception e) {
            ErrorDTO errorDTO = new ErrorDTO(e.getMessage());
            return new ResponseEntity<>(errorDTO, HttpStatus.BAD_REQUEST);
        }
    }
}
