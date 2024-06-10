package com.wwme.wwme.login.controller;

import com.wwme.wwme.login.domain.dto.response.ErrorDTO;
import com.wwme.wwme.login.exception.InvalidRefreshTokenException;
import com.wwme.wwme.login.exception.NullRefreshTokenException;
import com.wwme.wwme.login.service.JWTUtilService;
import com.wwme.wwme.login.service.ReissueService;
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

    @PostMapping("/reissue")
    public ResponseEntity<?> reissue(HttpServletRequest request,
                                     HttpServletResponse response) {
        try {
            Cookie[] cookies = request.getCookies();
            String refresh = reissueService.validateRefreshToken(cookies);
            String userKey = jwtUtilService.getUserKey(refresh);
            String role = jwtUtilService.getRole(refresh);
            String newAccess = reissueService.generateAccessToken(userKey, role);
            String newRefresh = reissueService.exchangeRefreshToken(userKey, role, refresh);

            log.info("User[{}] re-generate access and refresh token", userKey);
            response.setHeader("access", newAccess);
            response.addCookie(createCookie("refresh", newRefresh));

            return new ResponseEntity<>(HttpStatus.OK);
        } catch (NullRefreshTokenException | InvalidRefreshTokenException | Exception e) {
            log.info("User has expired or invalid refresh token");
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
