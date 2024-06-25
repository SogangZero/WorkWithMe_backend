package com.wwme.wwme.login.service;

import com.wwme.wwme.user.domain.User;
import com.wwme.wwme.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class NicknameService {
    private final UserRepository userRepository;

    public void saveNickname(String nickname, String userKey, Long profileImage) {
        if (nickname == null || nickname.isEmpty()) {
            throw new IllegalArgumentException("Nickname Must Have Contents");
        }

        User user = userRepository.findByUserKey(userKey).orElseThrow();

        user.ChangeNickname(nickname);
        user.setProfileImageId(profileImage);
        user.registerComplete();

        log.info("User {} set nickname {}", user.getUserKey(), user.getNickname());
    }
}
