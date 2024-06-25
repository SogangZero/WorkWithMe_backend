package com.wwme.wwme.login.filter;

import com.wwme.wwme.login.exception.JwtTokenException;
import com.wwme.wwme.login.service.JWTUtilService;
import com.wwme.wwme.login.repository.RefreshRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.filter.GenericFilterBean;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
public class CustomLogoutFilter extends GenericFilterBean {
    private final JWTUtilService jwtUtilService;
    private final RefreshRepository refreshRepository;

    @Override
    public void doFilter(ServletRequest request,
                         ServletResponse response,
                         FilterChain chain)
            throws IOException, ServletException {
        try {
            customDoFilter((HttpServletRequest) request, (HttpServletResponse) response, chain);
        } catch (JwtTokenException e) {
            throw new RuntimeException("Logout Fail");
        }
    }

    private void customDoFilter(HttpServletRequest request,
                                HttpServletResponse response,
                                FilterChain chain)
            throws IOException, ServletException, JwtTokenException {
        //verify path and method
        String requestUri = request.getRequestURI();
        if (!requestUri.matches("^\\/logout$")) {
            chain.doFilter(request, response);
            return;
        }
        String requestMethod = request.getMethod();
        if (!requestMethod.equals("POST")) {
            chain.doFilter(request, response);
            return;
        }

        //get refresh token
        String refresh = response.getHeader("refresh");

        if (refresh == null) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        try {
            jwtUtilService.isExpired(refresh);
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        //check token is refresh token
        String category = jwtUtilService.getCategory(refresh);
        if (!category.equals("refresh")) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        //check token is in DB
        Boolean isExist = refreshRepository.existsByRefresh(refresh);
        if (!isExist) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        //logout logic
        refreshRepository.deleteByRefresh(refresh);
        response.setStatus(HttpServletResponse.SC_OK);
    }


}
