package com.wwme.wwme.login.controller;

import com.wwme.wwme.login.jwt.JWTUtil;
import com.wwme.wwme.login.service.NicknameService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

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
                               @RequestParam("nickname") String nickname) {
        System.out.println("nickname = " + nickname);
        String authorization = request.getHeader("access");
//        Cookie[] cookies = request.getCookies();
//        for (Cookie cookie : cookies) {
//            if (cookie.getName().equals("access")) {
//                authorization = cookie.getValue();
//            }
//        }

        String userKey = jwtUtil.getUserKey(authorization);
        nicknameService.saveNicknameAndChangeRole(nickname, userKey);

        return "redirect:/";
    }
}
