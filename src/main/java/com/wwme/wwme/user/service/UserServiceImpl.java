package com.wwme.wwme.user.service;

import com.wwme.wwme.login.exception.JwtTokenException;
import com.wwme.wwme.login.jwt.JWTUtil;
import com.wwme.wwme.user.domain.User;
import com.wwme.wwme.user.domain.dto.UserInfoDTO;
import com.wwme.wwme.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService{
    @Override
    public UserInfoDTO getUserInfo(User user) {
        return new UserInfoDTO(true, user.getNickname(), 0);
    }
}
