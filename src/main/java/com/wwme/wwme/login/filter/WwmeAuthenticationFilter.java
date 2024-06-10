package com.wwme.wwme.login.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wwme.wwme.login.domain.CustomAuthenticationToken;
import com.wwme.wwme.login.domain.dto.CustomOAuth2User;
import com.wwme.wwme.login.domain.dto.response.ErrorDTO;
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
    private final ObjectMapper objectMapper;

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
            filterChain.doFilter(request, response);
            return;
        }

        String category = null;
        String userKey = null;
        String role = null;


        try {
            checkTokenExpiredDate(accessToken);

            category = jwtUtilService.getCategory(accessToken);
            userKey = jwtUtilService.getUserKey(accessToken);
            role = jwtUtilService.getRole(accessToken);
            if (!category.equals("access")) {
                log.info("AccessToken is invalid");
                throw new JwtTokenException("AccessToken is invalid");
            }

        } catch (JwtTokenException e) {
            log.info("AccessToken is invalid");

            ErrorDTO errorDTO = new ErrorDTO("Access Token Is Invalid. Unauthorized Access.");
            String result = objectMapper.writeValueAsString(errorDTO);
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write(result);
            return;
        }


        log.info("UserKey {}[{}] has access token {}", userKey, role, accessToken);
        UserDTO userDTO = UserDTO.builder()
                .userKey(userKey)
                .role(role)
                .build();

        CustomOAuth2User customOAuth2User = new CustomOAuth2User(userDTO);

        Authentication authToken = new CustomAuthenticationToken(customOAuth2User, userKey, customOAuth2User.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authToken);

        filterChain.doFilter(request, response);
    }

    private void checkTokenExpiredDate(String accessToken) throws JwtTokenException {
        if (jwtUtilService.isExpired(accessToken)) {
            log.info("AccessToken is expired");
            throw new JwtTokenException("AccessToken is expired");
        }
    }
}
