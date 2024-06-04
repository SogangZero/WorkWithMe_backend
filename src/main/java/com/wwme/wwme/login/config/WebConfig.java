package com.wwme.wwme.login.config;

import com.wwme.wwme.login.aop.LoginUserArgumentResolver;
import com.wwme.wwme.login.jwt.JWTUtil;
import com.wwme.wwme.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;
//TODO : test code에 의존성 추가해주어야지 test가 통과할 수 있음
@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {
    private final JWTUtil jwtUtil;
    private final UserRepository userRepository;

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(new LoginUserArgumentResolver(userRepository, jwtUtil));
    }
}
