package com.wwme.wwme.user.service;

import com.wwme.wwme.login.domain.dto.UserInfoReissueDTO;
import com.wwme.wwme.user.domain.User;
import com.wwme.wwme.user.domain.dto.UserInfoDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService{
    @Override
    public UserInfoDTO getUserInfo(User user) {
        return UserInfoDTO.builder()
                .success(true)
                .nickname(user.getNickname())
                .profile_image_id(user.getProfileImageId())
                .build();
    }

    @Override
    public UserInfoReissueDTO getUserInfoReissue(User user) {
        return UserInfoReissueDTO.builder()
                .user_id(user.getId())
                .nickname(user.getNickname())
                .profile_image_id(user.getProfileImageId())
                .build();
    }
}
