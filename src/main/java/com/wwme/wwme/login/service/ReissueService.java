package com.wwme.wwme.login.service;

import com.wwme.wwme.login.domain.entity.RefreshEntity;
import com.wwme.wwme.login.exception.InvalidRefreshTokenException;
import com.wwme.wwme.login.exception.JwtTokenException;
import com.wwme.wwme.login.exception.NullRefreshTokenException;
import com.wwme.wwme.login.repository.RefreshRepository;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
@RequiredArgsConstructor
public class ReissueService {
    private final JWTUtilService jwtUtilService;
    private final RefreshRepository refreshRepository;

    public String validateRefreshToken(String refresh)
            throws NullRefreshTokenException, JwtTokenException, InvalidRefreshTokenException {

        if (refresh == null) {
            throw new NullRefreshTokenException("Cannot Find Refresh Token At Request Header");
        }

        //expired check
        if (jwtUtilService.isExpired(refresh)) {
            throw new InvalidRefreshTokenException("Refresh Token Has Expired");
        }


        //check if the token is refresh
        String category = jwtUtilService.getCategory(refresh);
        if (!category.equals("refresh")) {
            throw new InvalidRefreshTokenException("Refresh Token Has Invalid Content [Category]");
        }

        //check if the refresh token is in DB
        Boolean isExist = refreshRepository.existsByRefresh(refresh);
        if (!isExist) {
            throw new InvalidRefreshTokenException("Refresh Token Has Invalid Content [Not Exist in DB]");
        }

        return refresh;
    }

    public String generateAccessToken(String userKey, String role) {
        String newAccess = jwtUtilService.createJwt("access", userKey, role, 10 * 60 * 1000L);//10 minutes
        return newAccess;
    }

    public String exchangeRefreshToken(String userKey, String role, String oldRefresh) {
        String newRefresh = jwtUtilService.createJwt("refresh", userKey, role, 24 * 60 * 60 * 1000L);//24 hours


        //delete old refresh token and save new refresh token
        refreshRepository.deleteByRefresh(oldRefresh);
        addRefreshToken(userKey, newRefresh, 24 * 60 * 60 * 1000L);

        return newRefresh;
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
