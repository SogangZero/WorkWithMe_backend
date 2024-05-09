package com.wwme.wwme.login.service;

import com.wwme.wwme.login.domain.entity.UserEntity;
import com.wwme.wwme.login.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class NicknameService {
    private final UserRepository userRepository;

    public void saveNicknameAndChangeRole(String nickname, String userKey) {
        System.out.println("NicknameService.saveNicknameAndChangeRole");
        System.out.println("userKey = " + userKey);
        System.out.println("nickname = " + nickname);
        UserEntity user = userRepository.findByUserKey(userKey);

        user.setNickname(nickname);
        user.setRole("ROLE_USER");
    }
}
