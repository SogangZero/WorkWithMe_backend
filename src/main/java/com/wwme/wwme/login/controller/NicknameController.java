package com.wwme.wwme.login.controller;

import com.wwme.wwme.login.jwt.JWTUtil;
import com.wwme.wwme.login.service.NicknameService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@Controller
@RequiredArgsConstructor
public class NicknameController {
    private final NicknameService nicknameService;
    private final JWTUtil jwtUtil;

    @GetMapping("/login/nickname")
    public String nickName() {
        return "login/nickname";
    }

    @PostMapping("/login/nickname")
    public String saveNickname(HttpServletRequest request,
                               @RequestBody() Map<String, String> param) {
        String nickname = param.get("nickname");
        String authorization = request.getHeader("access");
        String userKey = jwtUtil.getUserKey(authorization);
        try {
            nicknameService.saveNicknameAndChangeRole(nickname, userKey);
        } catch (IllegalArgumentException e) {
            return "redirect:/login/nickname";
        }

        log.info("User[{}] request nickname setting by {}", userKey, nickname);

        return "redirect:/";
    }
}