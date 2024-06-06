package com.wwme.wwme.login.jwt;

import com.wwme.wwme.login.config.ResolverConfig;
import com.wwme.wwme.login.exception.JwtTokenException;
import com.wwme.wwme.login.service.JWTUtilService;
import com.wwme.wwme.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@WebMvcTest(JWTUtilService.class)
@Import(ResolverConfig.class)
public class JwtUtilTest {
    @Autowired
    JWTUtilService jwtUtilService;

    @MockBean
    private UserRepository userRepository;

    private String testToken;
    private final String category = "testCategory";
    private final String userKey = "testUserKey";
    private final String role = "testRole";
    private final Long expiredMs = 1000 * 60 * 60L;

    @BeforeEach
    public void init() {
        testToken = jwtUtilService.createJwt(category, userKey, role, expiredMs);
    }

    @Test
    @DisplayName("getUserKey from valid Token")
    public void getUserKeyTest() throws Exception {
        String extractedUserKey = jwtUtilService.getUserKey(testToken);

        assertThat(extractedUserKey).isEqualTo(userKey);
    }

    @Test
    @DisplayName("getUserKey from invalid Token")
    public void getUserKeyFailTest() throws Exception {
        String invalidToken = "invalid";

        assertThrows(
                JwtTokenException.class,
                () -> jwtUtilService.getUserKey(invalidToken));
    }

    @Test
    @DisplayName("getUserKey from null Token")
    public void getUserKeyNullTest() throws Exception {
        assertThrows(
                JwtTokenException.class,
                () -> jwtUtilService.getUserKey(null));
    }

    @Test
    @DisplayName("getRole from valid Token")
    public void getRoleTest() throws Exception {
        String extractedUserRole = jwtUtilService.getRole(testToken);

        assertThat(extractedUserRole).isEqualTo(role);
    }

    @Test
    @DisplayName("getRole from invalid Token")
    public void getRoleFailTest() throws Exception {
        String invalidToken = "invalid";

        assertThrows(
                JwtTokenException.class,
                () -> jwtUtilService.getRole(invalidToken));
    }

    @Test
    @DisplayName("getRole from null Token")
    public void getRoleNullTest() throws Exception {
        assertThrows(
                JwtTokenException.class,
                () -> jwtUtilService.getRole(null));
    }

    @Test
    @DisplayName("getCategory from valid Token")
    public void getCategoryTest() throws Exception {
        String extractedCategory = jwtUtilService.getCategory(testToken);

        assertThat(extractedCategory).isEqualTo(category);
    }

    @Test
    @DisplayName("getCategory from invalid Token")
    public void getCategoryFailTest() throws Exception {
        String invalidToken = "invalid";

        assertThrows(
                JwtTokenException.class,
                () -> jwtUtilService.getCategory(invalidToken));
    }

    @Test
    @DisplayName("getCategory from null Token")
    public void getCategoryNullTest() throws Exception {
        assertThrows(
                JwtTokenException.class,
                () -> jwtUtilService.getCategory(null));
    }

    @Test
    @DisplayName("isExpired 아직 유효기간이 남음")
    public void notExpiredTokenTest() throws Exception {
        assertThat(jwtUtilService.isExpired(testToken)).isFalse();
    }

    @Test
    @DisplayName("isExpired 유효기간이 지남")
    public void expiredTokenTest() throws Exception {
        String expiredToken = jwtUtilService.createJwt("test", "test", "test", 0L);

        assertThat(jwtUtilService.isExpired(expiredToken)).isTrue();
    }

    @Test
    @DisplayName("isExpired 유효하지 않은 토큰")
    public void expiredInvalidTokenTest() throws Exception {
        assertThrows(JwtTokenException.class,
                () -> jwtUtilService.isExpired("invalidToken"));
    }

    @Test
    @DisplayName("isExpired null인 토큰")
    public void expiredNullTokenTest() throws Exception {
        assertThrows(JwtTokenException.class,
                () -> jwtUtilService.isExpired(null));
    }
}
