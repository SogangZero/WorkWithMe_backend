package com.wwme.wwme.login.controller;

import com.wwme.wwme.login.domain.dto.UserInfoReissueDTO;
import com.wwme.wwme.login.domain.dto.response.DataDTO;
import com.wwme.wwme.login.domain.dto.response.ErrorDTO;
import com.wwme.wwme.login.exception.InvalidRefreshTokenException;
import com.wwme.wwme.login.exception.NullRefreshTokenException;
import com.wwme.wwme.login.service.JWTUtilService;
import com.wwme.wwme.login.service.ReissueService;
import com.wwme.wwme.user.domain.User;
import com.wwme.wwme.user.domain.dto.UserInfoDTO;
import com.wwme.wwme.user.repository.UserRepository;
import com.wwme.wwme.user.service.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class ReissueController {
    private final JWTUtilService jwtUtilService;
    private final ReissueService reissueService;
    private final UserService userService;
    private final UserRepository userRepository;

    @PostMapping("/reissue")
    public ResponseEntity<?> reissue(HttpServletRequest request,
                                     HttpServletResponse response) {
        try {
            log.info("start /reissue");
            String refreshToken = response.getHeader("refresh");
            log.info("refresh1 {}", refreshToken);
            String refresh = reissueService.validateRefreshToken(refreshToken);
            log.info("refresh2 {}", refresh);
            String userKey = jwtUtilService.getUserKey(refresh);
            log.info("userkey {}", userKey);
            String role = jwtUtilService.getRole(refresh);
            log.info("role {}", role);
            String newAccess = reissueService.generateAccessToken(userKey, role);
            log.info("newAccess {}", newAccess);
            String newRefresh = reissueService.exchangeRefreshToken(userKey, role, refresh);
            log.info("newRefresh {}", newRefresh);
            User user = userRepository.findByUserKey(userKey)
                    .orElseThrow(() -> new IllegalArgumentException("not exist user"));
            log.info("user id {}", user.getId());
            UserInfoReissueDTO userInfoReissue = userService.getUserInfoReissue(user);

            log.info("User[{}] re-generate access and refresh token", userKey);
            response.setHeader("access", newAccess);
            response.setHeader("refresh", newRefresh);

            return new ResponseEntity<>(new DataDTO(userInfoReissue), HttpStatus.OK);
        } catch (NullRefreshTokenException | InvalidRefreshTokenException | Exception e) {
            log.error("User has expired or invalid refresh token" + e.getMessage());
            ErrorDTO errorDTO = new ErrorDTO(e.getMessage());
            return new ResponseEntity<>(errorDTO, HttpStatus.BAD_REQUEST);
        }
    }

    private Cookie createCookie(String key, String value) {
        Cookie cookie = new Cookie(key, value);
        cookie.setMaxAge(24 * 60 * 60);
        cookie.setHttpOnly(true);

        return cookie;
    }
}
