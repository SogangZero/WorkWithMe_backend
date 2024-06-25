package com.wwme.wwme.user.service;

import com.wwme.wwme.login.domain.dto.UserInfoReissueDTO;
import com.wwme.wwme.login.exception.JwtTokenException;
import com.wwme.wwme.user.domain.User;
import com.wwme.wwme.user.domain.dto.UserInfoDTO;

import java.util.NoSuchElementException;

public interface UserService {
    UserInfoDTO getUserInfo(User user);

    UserInfoReissueDTO getUserInfoReissue(User user);
}
