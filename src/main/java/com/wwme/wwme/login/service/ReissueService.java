package com.wwme.wwme.login.service;

import com.wwme.wwme.login.domain.entity.RefreshEntity;
import com.wwme.wwme.login.exception.InvalidRefreshTokenException;
import com.wwme.wwme.login.exception.JwtTokenException;
import com.wwme.wwme.login.exception.NullRefreshTokenException;
import com.wwme.wwme.login.jwt.JWTUtil;
import com.wwme.wwme.login.repository.RefreshRepository;
import jakarta.servlet.http.Cookie;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
@RequiredArgsConstructor
public class ReissueService {
    private final JWTUtil jwtUtil;
    private final RefreshRepository refreshRepository;

    public String validateRefreshToken(Cookie[] cookies) throws NullRefreshTokenException, JwtTokenException, InvalidRefreshTokenException {
        String refresh = null;
        for (Cookie cookie : cookies) {
            if (cookie.getName().equals("refresh")) {
                refresh = cookie.getValue();
            }
        }

        if (refresh == null) {
            throw new NullRefreshTokenException("refresh token null");
        }

        //expired check
        if (jwtUtil.isExpired(refresh)) {
            throw new InvalidRefreshTokenException("expired refresh token");
        }


        //check if the token is refresh
        String category = jwtUtil.getCategory(refresh);
        if (!category.equals("refresh")) {
            throw new InvalidRefreshTokenException("invalid refresh token");
        }

        //check if the refresh token is in DB
        Boolean isExist = refreshRepository.existsByRefresh(refresh);
        if (!isExist) {
            throw new InvalidRefreshTokenException("invalid refresh token");
        }

        return refresh;
    }

    public String generateAccessToken(String userKey, String role) {
        String newAccess = jwtUtil.createJwt("access", userKey, role, 10 * 60 * 1000L);//10 minutes
        return newAccess;
    }

    public String exchangeRefreshToken(String userKey, String role, String oldRefresh) {
        String newRefresh = jwtUtil.createJwt("refresh", userKey, role, 24 * 60 * 60 * 1000L);//24 hours


        //delete old refresh token and save new refresh token
        refreshRepository.deleteByRefresh(oldRefresh);
        addRefreshToken(userKey, newRefresh, 24 * 60 * 60 * 1000L);

        return newRefresh;
    }

    private void addRefreshToken(String userKey, String refresh, Long expiredMs) {
        Date date = new Date(System.currentTimeMillis() + expiredMs);
        RefreshEntity refreshEntity = new RefreshEntity();

        refreshEntity.setUserKey(userKey);
        refreshEntity.setRefresh(refresh);
        refreshEntity.setExpiration(date.toString());

        refreshRepository.save(refreshEntity);
    }
}
