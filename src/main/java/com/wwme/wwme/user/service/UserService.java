package com.wwme.wwme.user.service;

import com.wwme.wwme.user.domain.User;

import java.util.NoSuchElementException;

public interface UserService {
    User getUserFromJWTString(String jwtString) throws NoSuchElementException;
}
