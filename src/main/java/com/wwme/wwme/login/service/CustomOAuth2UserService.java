package com.wwme.wwme.login.service;

import com.wwme.wwme.login.domain.dto.*;
import com.wwme.wwme.login.domain.entity.UserEntity;
import com.wwme.wwme.login.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {
    private final UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);

        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        OAuth2Response oAuth2Response = null;
        if (registrationId.equals("naver")) {
            oAuth2Response = new NaverResponse(oAuth2User.getAttributes());
        } else if (registrationId.equals("google")) {
            oAuth2Response = new GoogleResponse(oAuth2User.getAttributes());
        } else if (registrationId.equals("kakao")) {
            oAuth2Response = new KakaoResponse(oAuth2User.getAttributes());
        } else {
            return null;
        }

        log.info("User login {}", oAuth2Response);

        //create user-specific ID value by info from resource server
        String provider = oAuth2Response.getProvider();
        String userKey = oAuth2Response.getProviderId();
        UserEntity existData = userRepository.findByUserKey(userKey);

        if (existData == null) {
            log.info("New User {}", oAuth2Response.getName());
            UserEntity userEntity = new UserEntity();
            userEntity.setUserKey(userKey);
            userEntity.setName(oAuth2Response.getName());
            userEntity.setSocialProvider(provider);
            userEntity.setRole("ROLE_USER");

            userRepository.save(userEntity);
            UserDTO userDTO = new UserDTO(oAuth2Response.getName(), userKey, provider, "ROLE_USER");

            return new CustomOAuth2User(userDTO);
        } else {
            log.info("Existed User {}", oAuth2Response.getName());
            existData.setName(oAuth2Response.getName());
            userRepository.save(existData);
            UserDTO userDTO = new UserDTO(oAuth2Response.getName(), existData.getUserKey(), existData.getSocialProvider(), existData.getRole());

            return new CustomOAuth2User(userDTO);
        }
    }
}
