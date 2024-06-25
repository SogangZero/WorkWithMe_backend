package com.wwme.wwme.login.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wwme.wwme.login.exhandler.CustomAccessDeniedHandler;
import com.wwme.wwme.login.exhandler.CustomAuthenticationEntryPoint;
import com.wwme.wwme.login.filter.CustomLogoutFilter;
import com.wwme.wwme.login.filter.WwmeAuthenticationFilter;
import com.wwme.wwme.login.service.JWTUtilService;
import com.wwme.wwme.login.filter.SupervisorFilter;
import com.wwme.wwme.login.oauth2.CustomSuccessHandler;
import com.wwme.wwme.login.repository.RefreshRepository;
import com.wwme.wwme.login.service.CustomOAuth2UserService;
import com.wwme.wwme.user.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.ExceptionHandlingConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.client.web.OAuth2LoginAuthenticationFilter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import java.util.Collections;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final CustomOAuth2UserService customOAuth2UserService;
    private final CustomSuccessHandler customSuccessHandler;
    private final JWTUtilService jwtUtilService;
    private final RefreshRepository refreshRepository;
    private final UserRepository userRepository;
    private final ObjectMapper objectMapper;
    private final CustomAccessDeniedHandler customAccessDeniedHandler;
    private final CustomAuthenticationEntryPoint customAuthenticationEntryPoint;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .cors(corsCustomizer -> corsCustomizer.configurationSource(new CorsConfigurationSource() {

                    @Override
                    public CorsConfiguration getCorsConfiguration(HttpServletRequest request) {

                        CorsConfiguration configuration = new CorsConfiguration();

                        configuration.setAllowedOrigins(Collections.singletonList("http://localhost:3000"));
                        configuration.setAllowedMethods(Collections.singletonList("*"));
                        configuration.setAllowCredentials(true);
                        configuration.setAllowedHeaders(Collections.singletonList("*"));
                        configuration.setMaxAge(3600L);

                        configuration.setExposedHeaders(Collections.singletonList("Set-Cookie"));
                        configuration.setExposedHeaders(Collections.singletonList("Authorization"));

                        return configuration;
                    }
                }));

        //csrf disable
        http
                .csrf(AbstractHttpConfigurer::disable);

        //authorization for request URI
        http
                .authorizeHttpRequests((auth) -> auth
                        .requestMatchers("/", "/login", "/favicon.ico", "/reissue", "/login/oauth2/code/**").permitAll()
                        .anyRequest().hasAnyRole("ADMIN", "USER")
                );

        //access denied handler
        http
                .exceptionHandling((exception) -> exception
                        .accessDeniedHandler(customAccessDeniedHandler)
                        .authenticationEntryPoint(customAuthenticationEntryPoint));


        //From login disable
        http
                .formLogin(AbstractHttpConfigurer::disable);

        //HTTP basic login disable
        http
                .httpBasic(AbstractHttpConfigurer::disable);

        //add LogoutFilter
        http
                .addFilterBefore(new CustomLogoutFilter(jwtUtilService, refreshRepository), LogoutFilter.class);

        //add JWTFilter
        http
                .addFilterAfter(new WwmeAuthenticationFilter(jwtUtilService, objectMapper), OAuth2LoginAuthenticationFilter.class);

        //add SupervisorFilter
        http
                .addFilterBefore(new SupervisorFilter(jwtUtilService, userRepository), LogoutFilter.class);

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
