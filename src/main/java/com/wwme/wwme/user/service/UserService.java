package com.wwme.wwme.user.service;

import com.wwme.wwme.user.domain.User;

public interface UserService {
    User getUserFromJWTString(String jwtString);
}
