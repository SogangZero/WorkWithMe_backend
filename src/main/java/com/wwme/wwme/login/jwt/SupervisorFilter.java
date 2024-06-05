package com.wwme.wwme.login.jwt;

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

@Slf4j
@RequiredArgsConstructor
public class SupervisorFilter extends OncePerRequestFilter {
    private final JWTUtil jwtUtil;
    private final UserRepository userRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {
        String supervisorToken = request.getHeader("developer");

        if (supervisorToken == null) {
            filterChain.doFilter(request, response);
            return;
        }

        if (!supervisorToken.equals("developer")) {
            filterChain.doFilter(request, response);
            return;
        }
        String userKey = "developmentUserKey";

        String token = jwtUtil.createJwt("access", userKey, "ROLE_ADMIN", 24 * 60 * 60 * 1000L);
        request.setAttribute("access", token);


        if (!userRepository.existsByUserKey(userKey)) {
            User user = new User();
            user.setRole("ROLE_ADMIN");
            user.setNickname("developer");
            user.setUserKey(userKey);
            user.setRegisterDate(LocalDateTime.now());

            userRepository.save(user);
        }
        log.info("supervisor is in");
        filterChain.doFilter(request, response);

    }
}
