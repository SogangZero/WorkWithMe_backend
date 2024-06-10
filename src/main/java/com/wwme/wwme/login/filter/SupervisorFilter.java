package com.wwme.wwme.login.filter;

import com.wwme.wwme.login.service.JWTUtilService;
import com.wwme.wwme.user.domain.User;
import com.wwme.wwme.user.repository.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Objects;

@Slf4j
@RequiredArgsConstructor
public class SupervisorFilter extends OncePerRequestFilter {
    private final JWTUtilService jwtUtilService;
    private final UserRepository userRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {
        String supervisorToken = request.getHeader("developer");

        if (!Objects.equals(supervisorToken, "developer")) {
            filterChain.doFilter(request, response);
            return;
        }

        String userKey = "developmentUserKey";
        String token = jwtUtilService.createJwt("access", userKey, "ROLE_ADMIN", 24 * 60 * 60 * 1000L);
        request.setAttribute("access", token);
        
        createSuperUserIfNotExist(userKey);
        filterChain.doFilter(request, response);
    }

    private void createSuperUserIfNotExist(String userKey) {
        if (!userRepository.existsByUserKey(userKey)) {
            User user = User.builder()
                    .role("ROLE_ADMIN")
                    .nickname("developer")
                    .userKey(userKey)
                    .registerDate(LocalDateTime.now())
                    .build();

            userRepository.save(user);
        }
    }
}
