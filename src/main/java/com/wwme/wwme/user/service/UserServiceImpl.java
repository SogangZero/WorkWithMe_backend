package com.wwme.wwme.user.service;

import com.wwme.wwme.login.jwt.JWTUtil;
import com.wwme.wwme.user.domain.User;
import com.wwme.wwme.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService{
    private final JWTUtil jwtUtil;
    private final UserRepository userRepository;

    @Override
    public User getUserFromJWTString(String jwtString) throws NoSuchElementException {
        String userKey = jwtUtil.getUserKey(jwtString);
        Optional<User> optionalUserKey = userRepository.findByUserKey(userKey);
        return optionalUserKey.orElseThrow();
    }
}