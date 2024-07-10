package com.wwme.wwme.login.service;

import com.wwme.wwme.group.domain.UserGroup;
import com.wwme.wwme.group.repository.UserGroupRepository;
import com.wwme.wwme.user.domain.User;
import com.wwme.wwme.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class WithdrawService {
    private final UserRepository userRepository;
    private final UserGroupRepository userGroupRepository;

    public void withdrawUser(User user) {
        userGroupRepository.deleteByUser(user);
        userRepository.delete(user);
    }
}
