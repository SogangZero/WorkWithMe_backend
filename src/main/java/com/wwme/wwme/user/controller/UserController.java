package com.wwme.wwme.user.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wwme.wwme.login.exception.JwtTokenException;
import com.wwme.wwme.user.domain.User;
import com.wwme.wwme.user.domain.dto.UserInfoDTO;
import com.wwme.wwme.user.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.NoSuchElementException;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/user")
public class UserController {
    private final UserService userService;
    private final ObjectMapper objectMapper;

    @GetMapping
    public ResponseEntity<?> userInfo(HttpServletRequest request,
                                   HttpServletResponse response) {
        String accessToken = request.getHeader("access");
        try {
            User user = userService.getUserFromJWTString(accessToken);
            UserInfoDTO userInfoDTO = new UserInfoDTO(true, user.getNickname(), 0);

            response.setContentType("application/json");
            response.setStatus(HttpServletResponse.SC_OK);
            String result = objectMapper.writeValueAsString(userInfoDTO);

            return new ResponseEntity<>(result, HttpStatus.OK);
        } catch (NoSuchElementException | JsonProcessingException | JwtTokenException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }
}
