package com.wwme.wwme.login.controller;

import com.wwme.wwme.login.config.SecurityTestConfig;
import com.wwme.wwme.login.exception.InvalidRefreshTokenException;
import com.wwme.wwme.login.exception.NullRefreshTokenException;
import com.wwme.wwme.login.jwt.JWTUtil;
import com.wwme.wwme.login.repository.RefreshRepository;
import com.wwme.wwme.login.service.ReissueService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = ReissueController.class)
@ExtendWith(MockitoExtension.class)
@Import(SecurityTestConfig.class)
public class ReissueControllerTest {
    @Autowired
    private MockMvc mvc;
    @MockBean
    private ReissueService reissueService;
    @MockBean
    private JWTUtil jwtUtil;

    @Test
    @DisplayName("/reisuue POST : 서비스에서 NullRefreshTokenException이 발생하면 400 http status 반환")
    @WithMockUser(username = "TestUser", roles = "USER")
    public void serviceNullRefreshTokenExceptionResponse400Error() throws Exception, InvalidRefreshTokenException, NullRefreshTokenException {

        when(reissueService.validateRefreshToken(any()))
                .thenThrow(NullRefreshTokenException.class);

        mvc.perform(MockMvcRequestBuilders
                        .post("/reissue"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("/reisuue POST : 서비스에서 InvalidRefreshTokenException이 발생하면 400 http status 반환")
    @WithMockUser(username = "TestUser", roles = "USER")
    public void serviceInvalidRefreshTokenExceptionResponse400Error() throws Exception, InvalidRefreshTokenException, NullRefreshTokenException {

        when(reissueService.validateRefreshToken(any()))
                .thenThrow(InvalidRefreshTokenException.class);

        mvc.perform(MockMvcRequestBuilders
                        .post("/reissue"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("/reisuue POST : 정상적으로 refresh, access token이 발행되면 200 http status 반환")
    @WithMockUser(username = "TestUser", roles = "USER")
    public void serviceResponse200Error() throws Exception, InvalidRefreshTokenException, NullRefreshTokenException {

        when(reissueService.validateRefreshToken(any()))
                .thenReturn("test");
        when(jwtUtil.getUserKey(any()))
                .thenReturn("testUserKey");
        when(jwtUtil.getRole(any()))
                .thenReturn("testRole");
        when(reissueService.generateAccessToken(any(), any()))
                .thenReturn("testAccessToken");
        when(reissueService.exchangeRefreshToken(any(), any(), anyString()))
                .thenReturn("testRefreshToken");

        mvc.perform(MockMvcRequestBuilders
                        .post("/reissue"))
                .andExpect(status().isOk())
                .andExpect(header().string("access", "testAccessToken"))
                .andExpect(cookie().value("refresh", "testRefreshToken"));
    }

    @Test
    @DisplayName("권한이 없는 경우에는 /login으로 리다이렉트")
    public void unauthorizedUserRedirectLoginPage() throws Exception {
        //given
        mvc.perform(MockMvcRequestBuilders
                        .post("/reisuue"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("http://localhost/login"));
    }

}
