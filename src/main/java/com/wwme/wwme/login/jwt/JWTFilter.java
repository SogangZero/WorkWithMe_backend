package com.wwme.wwme.login.jwt;

import com.wwme.wwme.login.domain.dto.CustomOAuth2User;
import com.wwme.wwme.login.domain.dto.UserDTO;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
public class JWTFilter extends OncePerRequestFilter {
    private final JWTUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        log.info("HTTP REQUEST : {}",request.getRequestURI());
        if (request.getRequestURI().equals("/") || request.getRequestURI().equals("/login")) {
            filterChain.doFilter(request, response);
            return;
        }
        //get cookies -> find cookie in the authorization key
        String authorization = null;
        Cookie[] cookies = request.getCookies();
        for (Cookie cookie : cookies) {
            log.info("Cookie : {}", cookie.getName());
            if (cookie.getName().equals("Authorization")) {
                authorization = cookie.getValue();
            }
        }

        //validate authorization header
        if (authorization == null) {
            log.info("token null");
            filterChain.doFilter(request, response);

            return;
        }

        String token = authorization;

        //validate token's expired time
        if (jwtUtil.isExpired(token)) {
            log.info("token expired");

            filterChain.doFilter(request, response);

            return;
        }

        String userKey = jwtUtil.getUserKey(token);
        String role = jwtUtil.getRole(token);

        UserDTO userDTO = new UserDTO();
        userDTO.setUserKey(userKey);
        userDTO.setRole(role);

        CustomOAuth2User customOAuth2User = new CustomOAuth2User(userDTO);

        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(customOAuth2User, null, customOAuth2User.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authToken);

        filterChain.doFilter(request, response);
    }
}
