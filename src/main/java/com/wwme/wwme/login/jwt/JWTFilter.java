package com.wwme.wwme.login.jwt;

import com.wwme.wwme.login.domain.CustomAuthenticationToken;
import com.wwme.wwme.login.domain.dto.CustomOAuth2User;
import com.wwme.wwme.login.domain.dto.UserDTO;
import com.wwme.wwme.login.exception.JwtTokenException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
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
        String accessToken = request.getHeader("access");
        if (accessToken == null) {
            log.info("AccessToken is null");
            filterChain.doFilter(request, response);
            return;
        }

        String category = null;
        String userKey = null;
        String role = null;


        try {
            if (jwtUtil.isExpired(accessToken)) {
                log.info("AccessToken is expired");
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }
            category = jwtUtil.getCategory(accessToken);
            userKey = jwtUtil.getUserKey(accessToken);
            role = jwtUtil.getRole(accessToken);

        } catch (JwtTokenException e) {
            log.info("AccessToken is invalid");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        if (!category.equals("access")) {
            log.info("AccessToken is invalid");

            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        log.info("UserKey {}[{}] has access token {}", userKey, role, accessToken);
        UserDTO userDTO = new UserDTO();
        userDTO.setUserKey(userKey);
        userDTO.setRole(role);

        CustomOAuth2User customOAuth2User = new CustomOAuth2User(userDTO);

        Authentication authToken = new CustomAuthenticationToken(customOAuth2User, userKey, customOAuth2User.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authToken);

        filterChain.doFilter(request, response);
    }
}
