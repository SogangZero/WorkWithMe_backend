package com.wwme.wwme.login.jwt;

import io.jsonwebtoken.Jwts;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;

public class JWTUtilTest {
    private JWTUtil jwtUtil;
    private Key secretKey;

    @BeforeEach
    void setUp() {
        String secret = "testTESTtestTESTtestTESTtestTESTtestTESTtestTESTtestTEST";
        secretKey = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), Jwts.SIG.HS256.key().build().getAlgorithm());
        jwtUtil = new JWTUtil(secret);
    }

    @Test
    @DisplayName("getUserKey를 통해 JWT에서 userkey 추출")
    void getUserKeyReturnUserKey() {
        String userKey = "testUserKey";

        String token = createToken(userKey, "USER", "test", 3600000L);
        String response = jwtUtil.getUserKey(token);

        assertThat(response).isEqualTo(userKey);
    }

    @Test
    @DisplayName("getRole를 통해 JWT에서 role 추출")
    void getRoleReturnRole() {
        String role = "USER";

        String token = createToken("testUserKey", role, "test", 3600000L);
        String response = jwtUtil.getRole(token);

        assertThat(response).isEqualTo(role);
    }

    @Test
    @DisplayName("getCategory를 통해 JWT에서 category 추출")
    void getCategoryReturnCategory() {
        String category = "category";

        String token = createToken("testUserKey", "USER", category, 3600000L);
        String response = jwtUtil.getCategory(token);

        assertThat(response).isEqualTo(category);
    }

    @Test
    @DisplayName("isExpired를 통해 JWT의 유효시간이 만료되었는지 확인")
    void isExpiredReturnFalse() {
        String token = createToken("testUserKey", "USER", "test", System.currentTimeMillis() + 3600000L);

        boolean expired = jwtUtil.isExpired(token);

        assertThat(expired).isFalse();
    }

    @Test
    @DisplayName("createJwt를 통해 JWT를 생성")
    void createJwtReturnToken() {
        String token = jwtUtil.createJwt("test", "testUserKey", "USER", 3600000L);

        assertThat(token).isNotNull();
    }

    private String createToken(String userKey, String role, String category, long expiration) {
        return io.jsonwebtoken.Jwts.builder()
                .claim("userKey", userKey)
                .claim("role", role)
                .claim("category", category)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(secretKey)
                .compact();
    }
}
