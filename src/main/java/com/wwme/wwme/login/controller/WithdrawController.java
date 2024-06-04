package com.wwme.wwme.login.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wwme.wwme.login.domain.dto.SuccessDTO;
import com.wwme.wwme.login.exception.JwtTokenException;
import com.wwme.wwme.login.jwt.JWTUtil;
import com.wwme.wwme.user.domain.User;
import com.wwme.wwme.user.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/withdraw")
public class WithdrawController {
    private final JWTUtil jwtUtil;
    private final UserRepository userRepository;
    private final ObjectMapper objectMapper;


    @PostMapping
    public void withdrawUser(HttpServletRequest request,
                             HttpServletResponse response) throws JwtTokenException {
        String accessToken = request.getHeader("access");
        String userKey = jwtUtil.getUserKey(accessToken);
        try {
            User user = userRepository.findByUserKey(userKey).orElseThrow(IllegalArgumentException::new);
            userRepository.delete(user);
            SuccessDTO successDTO = new SuccessDTO(true);
            response.setStatus(HttpServletResponse.SC_OK);
            response.setContentType("application/json");
            String result = objectMapper.writeValueAsString(successDTO);
            response.getWriter().write(result);

        } catch (IllegalArgumentException | IOException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }
    }

}
