package com.wwme.wwme.login.service;

import com.wwme.wwme.login.domain.dto.*;
import com.wwme.wwme.login.domain.dto.provider.GoogleResponse;
import com.wwme.wwme.login.domain.dto.provider.KakaoResponse;
import com.wwme.wwme.login.domain.dto.provider.NaverResponse;
import com.wwme.wwme.login.domain.dto.provider.OAuth2Response;
import com.wwme.wwme.user.domain.User;
import com.wwme.wwme.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2AuthorizationException;
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
        log.info("userRequest : {} {} {}",userRequest.getClientRegistration(), userRequest.getAccessToken(), userRequest.getAdditionalParameters());
        OAuth2User oAuth2User = super.loadUser(userRequest);

        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        OAuth2Response oAuth2Response = getoAuth2Response(registrationId, oAuth2User);
        if (oAuth2Response == null) return null;

        log.info("User login {}", oAuth2Response);

        //create user-specific ID value by info from resource server
        String provider = oAuth2Response.getProvider();
        String userKey = oAuth2Response.getProviderId();
        User existData = null;

        try {
            existData = userRepository.findByUserKey(userKey).orElseThrow();
            log.info("Existed User {}", oAuth2Response.getName());
            userRepository.save(existData);
            UserDTO userDTO = UserDTO.builder()
                    .name(oAuth2Response.getName())
                    .userKey(existData.getUserKey())
                    .socialProvider(existData.getSocialProvider())
                    .role(existData.getRole())
                    .build();

            return new CustomOAuth2User(userDTO);
        } catch (NoSuchElementException e) {
            log.info("New User {}", oAuth2Response.getName());

            User user = User.builder()
                    .userKey(userKey)
                    .socialProvider(provider)
                    .role("ROLE_USER")
                    .build();

            UserDTO userDTO = UserDTO.builder()
                    .name(oAuth2Response.getName())
                    .userKey(userKey)
                    .socialProvider(provider)
                    .role("ROLE_USER")
                    .build();

            userRepository.save(user);
            return new CustomOAuth2User(userDTO);
        } catch (OAuth2AuthorizationException e) {
            log.error(e.getMessage());
            throw e;
        }
    }

    private static OAuth2Response getoAuth2Response(String registrationId, OAuth2User oAuth2User) {
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
        return oAuth2Response;
    }
}
