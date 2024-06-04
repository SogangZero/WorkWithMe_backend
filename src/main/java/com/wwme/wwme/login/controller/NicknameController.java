package com.wwme.wwme.login.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wwme.wwme.login.domain.dto.NicknameDTO;
import com.wwme.wwme.login.domain.dto.SuccessDTO;
import com.wwme.wwme.login.exception.JwtTokenException;
import com.wwme.wwme.login.jwt.JWTUtil;
import com.wwme.wwme.login.service.NicknameService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@Slf4j
@RestController
@RequiredArgsConstructor
public class NicknameController {
    private final NicknameService nicknameService;
    private final JWTUtil jwtUtil;
    private final ObjectMapper objectMapper;


    @PostMapping("/login/nickname")
    public void saveNickname(HttpServletRequest request,
                             HttpServletResponse response,
                             @RequestBody NicknameDTO nicknameDTO)
            throws IOException {

        try {
            String nickname = nicknameDTO.getNickname();
            String authorization = request.getHeader("access");
            String userKey = jwtUtil.getUserKey(authorization);
            log.info("User[{}] request nickname setting by {}", userKey, nickname);

            nicknameService.saveNicknameAndChangeRole(nickname, "ROLE_USER", userKey);
            response.setStatus(HttpServletResponse.SC_OK);
            response.setContentType("application/json");
            response.setCharacterEncoding("utf-8");
            SuccessDTO successDTO = new SuccessDTO(true);
            String result = objectMapper.writeValueAsString(successDTO);
            response.getWriter().write(result);

        } catch (IllegalArgumentException |JwtTokenException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }

    }
}
