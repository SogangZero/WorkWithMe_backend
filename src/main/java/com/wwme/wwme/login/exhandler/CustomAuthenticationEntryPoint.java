package com.wwme.wwme.login.exhandler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wwme.wwme.login.domain.dto.response.ErrorDTO;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {
    private final ObjectMapper objectMapper;

    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException)
            throws IOException, ServletException {
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType("application/json");

        ErrorDTO errorDTO = new ErrorDTO("Not Authenticated Access - 403 error occurred");
        String result = objectMapper.writeValueAsString(errorDTO);
        response.getWriter().write(result);
    }
}

