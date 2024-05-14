package com.wwme.wwme.user.service;

import com.wwme.wwme.login.jwt.JWTUtil;
import com.wwme.wwme.user.domain.User;
import com.wwme.wwme.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class UserServiceTest {
    @Autowired
    private JWTUtil jwtUtil;

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Test
    void getUserFromJwtStringSuccess() {
        String userKey = "user-key";
        String role = "some-role";
        User user = new User();
        user.setUserKey(userKey);
        user.setRole(role);
        user = userRepository.save(user);

        String jwtString = jwtUtil.createJwt("test", user.getUserKey(), user.getRole(), 100000L);

        User userFromJwt = userService.getUserFromJWTString(jwtString);

        assertThat(userFromJwt.getId()).isEqualTo(user.getId());
        assertThat(userFromJwt.getUserKey()).isEqualTo(user.getUserKey());
    }

}
