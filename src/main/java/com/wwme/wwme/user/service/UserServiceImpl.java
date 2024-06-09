package com.wwme.wwme.user.service;

import com.wwme.wwme.user.domain.User;
import com.wwme.wwme.user.domain.dto.UserInfoDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService{
    @Override
    public UserInfoDTO getUserInfo(User user) {
        return new UserInfoDTO(true, user.getNickname(), 0);
    }
}
