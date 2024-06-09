package com.wwme.wwme.login.config;

import com.wwme.wwme.login.aop.LoginUserArgumentResolver;
import com.wwme.wwme.login.service.JWTUtilService;
import com.wwme.wwme.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
@RequiredArgsConstructor
public class ResolverConfig implements WebMvcConfigurer {
    private final JWTUtilService jwtUtilService;
    private final UserRepository userRepository;

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(new LoginUserArgumentResolver(userRepository, jwtUtilService));
    }
}
