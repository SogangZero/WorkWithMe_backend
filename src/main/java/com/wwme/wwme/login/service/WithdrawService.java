package com.wwme.wwme.login.service;

import com.wwme.wwme.user.domain.User;
import com.wwme.wwme.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class WithdrawService {
    private final UserRepository userRepository;

    public void withdrawUser(User user) {
        userRepository.delete(user);
    }
}
