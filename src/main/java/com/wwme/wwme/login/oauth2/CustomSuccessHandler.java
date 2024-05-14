package com.wwme.wwme.login.oauth2;

import com.wwme.wwme.login.domain.dto.CustomOAuth2User;
import com.wwme.wwme.login.domain.entity.RefreshEntity;
import com.wwme.wwme.login.jwt.JWTUtil;
import com.wwme.wwme.login.repository.RefreshRepository;
import com.wwme.wwme.user.domain.User;
import com.wwme.wwme.user.repository.UserRepository;
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
    private final UserRepository userRepository;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication)
            throws IOException, ServletException {
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
        response.addCookie(createCookie("refresh", refresh));
        response.setStatus(HttpStatus.OK.value());

        log.info("Log Success Handler {}[{}]", userKey, role);
        log.info("access Token : {}", access);
        log.info("refresh Token : {}", refresh);

        User user = userRepository.findByUserKey(userKey).orElseThrow(); //NoSuchElementException
        if (user.getNickname() == null) {
            response.sendRedirect("http://localhost:8080/login/nickname");
        } else {
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
        cookie.setHttpOnly(true);

        return cookie;
    }
}
