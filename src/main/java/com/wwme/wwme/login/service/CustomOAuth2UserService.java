package com.wwme.wwme.login.service;

import com.wwme.wwme.login.domain.dto.*;
import com.wwme.wwme.user.domain.User;
import com.wwme.wwme.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;

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
        User existData = null;
        try {
            existData = userRepository.findByUserKey(userKey).orElseThrow();
        } catch (NoSuchElementException e) {
            log.info("New User {}", oAuth2Response.getName());
            User user = new User();
            user.setUserKey(userKey);
            user.setSocialProvider(provider);
            user.setRole("ROLE_USER");

            userRepository.save(user);
            UserDTO userDTO = new UserDTO(oAuth2Response.getName(),
                    userKey,
                    provider,
                    "ROLE_USER");

            return new CustomOAuth2User(userDTO);
        }

        log.info("Existed User {}", oAuth2Response.getName());
        userRepository.save(existData);
        UserDTO userDTO = new UserDTO(oAuth2Response.getName(),
                existData.getUserKey(),
                existData.getSocialProvider(),
                existData.getRole());

        return new CustomOAuth2User(userDTO);
    }
}
