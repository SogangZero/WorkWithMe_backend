package com.wwme.wwme.login.service;

import com.wwme.wwme.login.config.ResolverConfig;
import com.wwme.wwme.login.exception.InvalidRefreshTokenException;
import com.wwme.wwme.login.exception.NullRefreshTokenException;
import com.wwme.wwme.login.repository.RefreshRepository;
import com.wwme.wwme.user.repository.UserRepository;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.matches;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@WebMvcTest(ReissueService.class)
@Import(ResolverConfig.class)
public class ReissueServiceTest {
    @Autowired
    private ReissueService reissueService;
    @MockBean
    private JWTUtilService jwtUtilService;
    @MockBean
    private RefreshRepository refreshRepository;
    @MockBean
    private UserRepository userRepository;

    @Test
    @DisplayName("validateRefreshToken에서 cookie에 refresh token이 없으면 NullRefreshTokenException 발생")
    public void validateRefreshTokenHasNoRefreshTokenThrowException() throws Exception {
        //given
        Cookie[] cookies = new Cookie[]{new Cookie("Test", "Test")};

        Assertions.assertThrows(NullRefreshTokenException.class,
                () -> reissueService.validateRefreshToken(cookies));
    }

    @Test
    @DisplayName("validateRefreshToken에서 cookie에 refresh token이 expire되면 ExpiredJwtException 발생")
    public void validateRefreshTokenHasExpiredRefreshTokenThrowException() throws Exception {
        //given
        Cookie[] cookies = new Cookie[]{new Cookie("refresh", "refreshTest")};

        when(jwtUtilService.isExpired(any()))
                .thenThrow(ExpiredJwtException.class);

        Assertions.assertThrows(ExpiredJwtException.class,
                () -> reissueService.validateRefreshToken(cookies));
    }

    @Test
    @DisplayName("validateRefreshToken에서 cookie에 refresh token의 category가 refresh가 아니 InvalidRefreshTokenException 발생")
    public void validateRefreshTokenHasUnMatchCategoryThrowException() throws Exception {
        //given
        Cookie[] cookies = new Cookie[]{new Cookie("refresh", "refreshTest")};

        when(jwtUtilService.isExpired(any()))
                .thenReturn(true);
        when(jwtUtilService.getCategory(any()))
                .thenReturn("notRefresh");

        Assertions.assertThrows(InvalidRefreshTokenException.class,
                () -> reissueService.validateRefreshToken(cookies));
    }

    @Test
    @DisplayName("validateRefreshToken에서 cookie에 refresh token이 DB에 없는 경우 InvalidRefreshTokenException 발생")
    public void validateRefreshTokenNotInDBThrowException() throws Exception {
        //given
        Cookie[] cookies = new Cookie[]{new Cookie("refresh", "refreshTest")};
        when(jwtUtilService.isExpired(any()))
                .thenReturn(true);
        when(jwtUtilService.getCategory(any()))
                .thenReturn("refresh");
        when(refreshRepository.existsByRefresh(any()))
                .thenReturn(false);

        Assertions.assertThrows(InvalidRefreshTokenException.class,
                () -> reissueService.validateRefreshToken(cookies));
    }

    @Test
    @DisplayName("validateRefreshToken에서 cookie에 refresh token이 유효하다면 refresh 토큰 반환")
    public void validateRefreshTokenSuccess() throws Exception, InvalidRefreshTokenException, NullRefreshTokenException {
        //given
        Cookie[] cookies = new Cookie[]{new Cookie("refresh", "refreshTest")};

        when(jwtUtilService.isExpired(any()))
                .thenReturn(true);
        when(jwtUtilService.getCategory(any()))
                .thenReturn("refresh");
        when(refreshRepository.existsByRefresh(any()))
                .thenReturn(true);
        when(jwtUtilService.isExpired(any()))
                .thenReturn(false);

        assertThat(reissueService.validateRefreshToken(cookies)).isEqualTo("refreshTest");
    }

    @Test
    @DisplayName("generateAccessToken에서 새로운 access token 발행")
    public void generateAccessTokenTest() throws Exception {
        //given
        when(jwtUtilService.createJwt(matches("access"), any(), any(), any()))
                .thenReturn("testAccessToken");

        assertThat(reissueService.generateAccessToken("test", "test")).isEqualTo("testAccessToken");
    }

    @Test
    @DisplayName("exchangeRefreshToken에서 기존 refresh 토큰을 삭제하고 새로운 refresh 토큰 발행")
    public void exchangeRefreshTokenTest() throws Exception {
        //given
        when(jwtUtilService.createJwt(matches("refresh"), any(), any(), any()))
                .thenReturn("newRefreshToken");

        assertThat(reissueService.exchangeRefreshToken("testKey", "USER", "oldRefreshToken"))
                .isEqualTo("newRefreshToken");
    }
}
