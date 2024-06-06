package com.wwme.wwme.login.service;

import com.wwme.wwme.user.domain.User;
import com.wwme.wwme.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class NicknameService {
    private final UserRepository userRepository;

    public void saveNickname(String nickname, String userKey) {
        if (nickname == null || nickname.isEmpty()) {
            throw new IllegalArgumentException();
        }

        User user = userRepository.findByUserKey(userKey).orElseThrow();

        user.setNickname(nickname);
        user.setRegisterDate(LocalDateTime.now());

        log.info("User {} set nickname {}", user.getUserKey(), user.getNickname());
    }
}
