package com.wwme.wwme.login.oauth2;

import com.wwme.wwme.login.domain.dto.CustomOAuth2User;
import com.wwme.wwme.login.domain.entity.RefreshEntity;
import com.wwme.wwme.login.jwt.JWTUtil;
import com.wwme.wwme.login.repository.RefreshRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;

@Component
@Slf4j
@RequiredArgsConstructor
public class CustomSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    private final JWTUtil jwtUtil;
    private final RefreshRepository refreshRepository;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication)
            throws IOException, ServletException {
//        //단일 토큰 방식 -> 주석 처리
//        CustomOAuth2User customUserDetails = (CustomOAuth2User) authentication.getPrincipal();
//        String userKey = customUserDetails.getUserKey();
//
//        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
//        Iterator<? extends GrantedAuthority> iterator = authorities.iterator();
//        GrantedAuthority auth = iterator.next();
//        String role = auth.getAuthority();
//
//        String token = jwtUtil.createJwt(userKey, role, 1000 * 60 * 60L);
//        log.info("String role : {}", role);
//        response.addCookie(createCookie("Authorization", token));
//        if (role.equals("ROLE_TEMP")) {
//            log.info("ROLE TEMP USER LOGGED IN");
//            response.sendRedirect("http://localhost:8080/login/nickname"); //닉네임 설정을 안 한 유저가 로그인 성공
//        } else {
//            log.info("ROLE USER USER LOGGED IN");
//            response.sendRedirect("http://localhost:8080/"); //닉네임 설정을 한 유저가 로그인 성공
//        }
        //get user info
        CustomOAuth2User customUserDetails = (CustomOAuth2User) authentication.getPrincipal();
        String userKey = customUserDetails.getUserKey();

        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        Iterator<? extends GrantedAuthority> iterator = authorities.iterator();
        GrantedAuthority auth = iterator.next();
        String role = auth.getAuthority();

        //generate token
        String access = jwtUtil.createJwt("access", userKey, role, 10 * 60 * 1000L);//10m
        String refresh = jwtUtil.createJwt("refresh", userKey, role, 24 * 60 * 60 * 1000L);//24h

        //store refresh token
        addRefreshToken(userKey, refresh, 24 * 60 * 60 * 1000L);

        //response setting
        response.setHeader("access", access);
        System.out.println("access = " + access);
        response.addCookie(createCookie("refresh", refresh));
        response.setStatus(HttpStatus.OK.value());

        if (role.equals("ROLE_TEMP")) {
            response.sendRedirect("http://localhost:8080/login/nickname");
        } else if (role.equals("ROLE_USER")) {
            response.sendRedirect("http://localhost:8080");
        }

    }

    private void addRefreshToken(String userKey, String refresh, Long expiredMs) {
        Date date = new Date(System.currentTimeMillis() + expiredMs);

        RefreshEntity refreshEntity = new RefreshEntity();
        refreshEntity.setUserKey(userKey);
        refreshEntity.setRefresh(refresh);
        refreshEntity.setExpiration(date.toString());

        refreshRepository.save(refreshEntity);
    }

    private Cookie createCookie(String key, String value) {
        Cookie cookie = new Cookie(key, value);
        cookie.setMaxAge(24 * 60 * 60);
//        cookie.setPath("/");
        cookie.setHttpOnly(true);

        return cookie;
    }

}
