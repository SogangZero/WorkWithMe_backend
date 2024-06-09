package com.wwme.wwme.login.filter;

import com.wwme.wwme.login.domain.CustomAuthenticationToken;
import com.wwme.wwme.login.domain.dto.CustomOAuth2User;
import com.wwme.wwme.login.domain.dto.UserDTO;
import com.wwme.wwme.login.exception.JwtTokenException;
import com.wwme.wwme.login.service.JWTUtilService;
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
public class WwmeAuthenticationFilter extends OncePerRequestFilter {
    private final JWTUtilService jwtUtilService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {
        String accessToken = request.getHeader("access");

        //For Supervisor
        String access = (String)request.getAttribute("access");
        if (access != null) {
            accessToken = access;
        }

        if (accessToken == null) {
            log.info("AccessToken is null");
            filterChain.doFilter(request, response);
            return;
        }

        String category = null;
        String userKey = null;
        String role = null;


        try {
            if (jwtUtilService.isExpired(accessToken)) {
                log.info("AccessToken is expired");
                throw new JwtTokenException("AccessToken is expired");
            }

            category = jwtUtilService.getCategory(accessToken);
            userKey = jwtUtilService.getUserKey(accessToken);
            role = jwtUtilService.getRole(accessToken);
            if (!category.equals("access")) {
                log.info("AccessToken is invalid");
                throw new JwtTokenException("AccessToken is invalid");
            }

        } catch (JwtTokenException e) {
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
