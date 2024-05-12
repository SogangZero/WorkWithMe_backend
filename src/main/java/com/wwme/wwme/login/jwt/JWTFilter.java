package com.wwme.wwme.login.jwt;

import com.wwme.wwme.login.domain.dto.CustomOAuth2User;
import com.wwme.wwme.login.domain.dto.UserDTO;
import com.wwme.wwme.login.domain.entity.UserEntity;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.io.PrintWriter;

@Slf4j
@RequiredArgsConstructor
public class JWTFilter extends OncePerRequestFilter {
    private final JWTUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {
//        //단일 토큰 시스템
//        log.info("HTTP REQUEST : {}",request.getRequestURI());
//        if (request.getRequestURI().equals("/") || request.getRequestURI().equals("/login")) {
//            filterChain.doFilter(request, response);
//            return;
//        }
//        //get cookies -> find cookie in the authorization key
//        String authorization = null;
//        Cookie[] cookies = request.getCookies();
//        for (Cookie cookie : cookies) {
//            log.info("Cookie : {}", cookie.getName());
//            if (cookie.getName().equals("Authorization")) {
//                authorization = cookie.getValue();
//            }
//        }
//
//        //validate authorization header
//        if (authorization == null) {
//            log.info("token null");
//            //filterChain.doFilter(request, response);
//            response.sendRedirect("http://localhost:8080/");
//            return;
//        }
//
//        String token = authorization;
//
//        //validate token's expired time
//        try {
//            if (jwtUtil.isExpired(token)) {
//                log.info("token expired");
//                response.sendRedirect("http://localhost:8080/login");
//                return;
//            }
//        } catch (ExpiredJwtException e) {
//            response.sendRedirect("http://localhost:8080/login");
//            log.info("Expired Jwt");
//            return;
//        }
//
//
//        String userKey = jwtUtil.getUserKey(token);
//        String role = jwtUtil.getRole(token);
//
//        UserDTO userDTO = new UserDTO();
//        userDTO.setUserKey(userKey);
//        userDTO.setRole(role);
//
//        CustomOAuth2User customOAuth2User = new CustomOAuth2User(userDTO);
//
//        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(customOAuth2User, null, customOAuth2User.getAuthorities());
//        SecurityContextHolder.getContext().setAuthentication(authToken);
//
//        filterChain.doFilter(request, response);

        String accessToken = request.getHeader("access");
        System.out.println("accessToken = " + accessToken);
        if (accessToken == null) {
            System.out.println("AccessToken is null");
            filterChain.doFilter(request, response);
            return;
        }

        try {
            jwtUtil.isExpired(accessToken);
        } catch (ExpiredJwtException e) {
            System.out.println("AccessToken is expired");
            PrintWriter writer = response.getWriter();
            writer.print("Access token expired");

            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        String category = jwtUtil.getCategory(accessToken);
        if (!category.equals("access")) {
            System.out.println("AccessToken is invalid");
            PrintWriter writer = response.getWriter();
            writer.print("invalid access token");

            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        String userKey = jwtUtil.getUserKey(accessToken);
        String role = jwtUtil.getRole(accessToken);
        System.out.println("userKey = " + userKey);
        UserDTO userDTO = new UserDTO();
        userDTO.setUserKey(userKey);
        userDTO.setRole(role);

        CustomOAuth2User customOAuth2User = new CustomOAuth2User(userDTO);

        Authentication authToken = new UsernamePasswordAuthenticationToken(customOAuth2User, null, customOAuth2User.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authToken);

        filterChain.doFilter(request, response);
    }
}
