package com.wwme.wwme.login.service;

import com.wwme.wwme.login.domain.entity.UserEntity;
import com.wwme.wwme.login.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class NicknameService {
    private final UserRepository userRepository;

    public void saveNicknameAndChangeRole(String nickname, String userKey) {
        if (nickname == null || nickname.isEmpty()) {
            throw new IllegalArgumentException();
        }

        UserEntity user = userRepository.findByUserKey(userKey);

        user.setNickname(nickname);
        user.setRole("ROLE_USER");
        user.setRegistrationDate(LocalDateTime.now());

        log.info("User {} set nickname {}", user.getName(), user.getNickname());
    }
}
