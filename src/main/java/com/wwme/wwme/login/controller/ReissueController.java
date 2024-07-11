package com.wwme.wwme.login.controller;

import com.wwme.wwme.login.domain.dto.UserInfoReissueDTO;
import com.wwme.wwme.login.domain.dto.response.DataDTO;
import com.wwme.wwme.login.domain.dto.response.ErrorDTO;
import com.wwme.wwme.login.exception.InvalidRefreshTokenException;
import com.wwme.wwme.login.exception.NullRefreshTokenException;
import com.wwme.wwme.login.service.JWTUtilService;
import com.wwme.wwme.login.service.ReissueService;
import com.wwme.wwme.user.domain.User;
import com.wwme.wwme.user.repository.UserRepository;
import com.wwme.wwme.user.service.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@Transactional
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
            String refreshToken = request.getHeader("refresh");
            log.info("/reissue[POST] refresh : {}", refreshToken);
            String refresh = reissueService.validateRefreshToken(refreshToken);
            String userKey = jwtUtilService.getUserKey(refresh);
            String role = jwtUtilService.getRole(refresh);
            String newAccess = reissueService.generateAccessToken(userKey, role);
            String newRefresh = reissueService.exchangeRefreshToken(userKey, role, refresh);
            User user = userRepository.findByUserKey(userKey)
                    .orElseThrow(() -> new IllegalArgumentException("not exist user"));
            UserInfoReissueDTO userInfoReissue = userService.getUserInfoReissue(user);

            log.info("User[{}] re-generate access[{}] and refresh[{}] token ", userKey, newAccess, newRefresh);
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
