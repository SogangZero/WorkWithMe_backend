package com.wwme.wwme.login.oauth2;

import com.wwme.wwme.login.domain.TokenTime;
import com.wwme.wwme.login.domain.dto.CustomOAuth2User;
import com.wwme.wwme.login.domain.entity.RefreshEntity;
import com.wwme.wwme.login.service.JWTUtilService;
import com.wwme.wwme.login.repository.RefreshRepository;
import com.wwme.wwme.user.domain.User;
import com.wwme.wwme.user.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.NoSuchElementException;

@Component
@Slf4j
@RequiredArgsConstructor
public class CustomSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    private final JWTUtilService jwtUtilService;
    private final RefreshRepository refreshRepository;
    private final UserRepository userRepository;

    private static final Long refreshTokenDurationMS = TokenTime.refreshTokenExpirationMS;
    private static final Long accessTokenDurationMS = TokenTime.accessTokenExpirationMS;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication)
            throws IOException {
        //get user info
        CustomOAuth2User customUserDetails = (CustomOAuth2User) authentication.getPrincipal();
        String userKey = customUserDetails.getUserKey();

        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        Iterator<? extends GrantedAuthority> iterator = authorities.iterator();
        GrantedAuthority auth = iterator.next();
        String role = auth.getAuthority();

        //generate token
        String access = jwtUtilService.createJwt("access", userKey, role, accessTokenDurationMS);//10m
        String refresh = jwtUtilService.createJwt("refresh", userKey, role, refreshTokenDurationMS);//24h

        //store refresh token
        addRefreshToken(userKey, refresh, refreshTokenDurationMS);

        log.info("Log Success Handler {}[{}]", userKey, role);
        log.info("access Token : {}", access);
        log.info("refresh Token : {}", refresh);

        try {
            User user = userRepository.findByUserKey(userKey).orElseThrow(NoSuchElementException::new); //NoSuchElementException
            String redirectURL = "webauthcallback://"+
                    "?access=" + access +
                    "&refresh=" + refresh +
                    "&state=ZeroAuth" + user.getSocialProvider();
            response.setStatus(HttpServletResponse.SC_TEMPORARY_REDIRECT);
            response.setHeader("Location", redirectURL);
        } catch (NoSuchElementException e) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        }
    }

    private void addRefreshToken(String userKey, String refresh, Long expiredMs) {
        Date date = new Date(System.currentTimeMillis() + expiredMs);

        RefreshEntity refreshEntity = RefreshEntity.builder()
                .userKey(userKey)
                .refresh(refresh)
                .expiration(date.toString())
                .build();

        refreshRepository.save(refreshEntity);
    }
}
