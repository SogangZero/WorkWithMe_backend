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

        log.info("oAuth2User = {}", oAuth2User);

        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        OAuth2Response oAuth2Response = null;
        if (registrationId.equals("naver")) {
            oAuth2Response = new NaverResponse(oAuth2User.getAttributes());
            log.info("naver : {}", oAuth2Response);
        } else if (registrationId.equals("google")) {
            oAuth2Response = new GoogleResponse(oAuth2User.getAttributes());
            log.info("google : {}", oAuth2Response);
        } else if (registrationId.equals("kakao")) {
            oAuth2Response = new KakaoResponse(oAuth2User.getAttributes());
            log.info("kakao : {}", oAuth2Response);
        } else {
            return null;
        }

        //create user-specific ID value by info from resource server
        String username = oAuth2Response.getProvider() + " " + oAuth2Response.getProviderId();
        UserEntity existData = userRepository.findByUsername(username);

        if (existData == null) {
            UserEntity userEntity = new UserEntity();
            userEntity.setUsername(username);
            userEntity.setName(oAuth2Response.getName());
            userEntity.setRole("ROLE_USER");

            userRepository.save(userEntity);
            UserDTO userDTO = new UserDTO(oAuth2Response.getName(),username, "ROLE_USER");
            return new CustomOAuth2User(userDTO);
        } else {
            existData.setName(oAuth2Response.getName());
            userRepository.save(existData);
            UserDTO userDTO = new UserDTO(oAuth2Response.getName(), existData.getUsername(), existData.getRole());

            return new CustomOAuth2User(userDTO);
        }
    }
}
