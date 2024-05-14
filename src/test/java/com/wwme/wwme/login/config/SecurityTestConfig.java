package com.wwme.wwme.login.config;

import com.wwme.wwme.login.domain.dto.CustomOAuth2User;
import com.wwme.wwme.login.jwt.CustomLogoutFilter;
import com.wwme.wwme.login.jwt.JWTFilter;
import com.wwme.wwme.login.jwt.JWTUtil;
import com.wwme.wwme.login.oauth2.CustomSuccessHandler;
import com.wwme.wwme.login.repository.RefreshRepository;
import com.wwme.wwme.login.service.CustomOAuth2UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.annotation.RequiredTypes;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.MockBeans;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.client.web.OAuth2LoginAuthenticationFilter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import java.util.Collections;

@TestConfiguration
@EnableWebSecurity
@MockBeans({
        @MockBean(JWTUtil.class),
        @MockBean(RefreshRepository.class),
        @MockBean(CustomOAuth2UserService.class),
        @MockBean(CustomSuccessHandler.class)
})
public class SecurityTestConfig {
    @Autowired private JWTUtil jwtUtil;
    @Autowired private RefreshRepository refreshRepository;
    @Autowired private CustomOAuth2UserService customOAuth2UserService;
    @Autowired private CustomSuccessHandler customSuccessHandler;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        //csrf disable
        http
                .csrf(AbstractHttpConfigurer::disable);

        //authorization for request URI
        http
                .authorizeHttpRequests((auth) -> auth
                        .requestMatchers("/", "/login", "/favicon.ico").permitAll()
                        .requestMatchers("/reissue").permitAll()
                        .requestMatchers("/login/oauth2/code/**").permitAll()
                        .anyRequest().authenticated()
                );

//From login disable
        http
                .formLogin(AbstractHttpConfigurer::disable);

        //HTTP basic login disable
        http
                .httpBasic(AbstractHttpConfigurer::disable);

        //add LogoutFilter
        http
                .addFilterBefore(new CustomLogoutFilter(jwtUtil, refreshRepository), LogoutFilter.class);

        //add JWTFilter
        http
                .addFilterAfter(new JWTFilter(jwtUtil), OAuth2LoginAuthenticationFilter.class);
        //oauth2
        http
                .oauth2Login((oauth2) -> oauth2
                        .userInfoEndpoint((userInfoEndpointConfig -> userInfoEndpointConfig
                                .userService(customOAuth2UserService)))
                        .successHandler(customSuccessHandler)
                );

        //session setting : STATELESS
        http
                .sessionManagement((session) -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        return http.build();
    }
}
