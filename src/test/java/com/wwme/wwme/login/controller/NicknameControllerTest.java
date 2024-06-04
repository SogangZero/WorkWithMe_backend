package com.wwme.wwme.login.controller;

import com.wwme.wwme.login.config.SecurityTestConfig;
import com.wwme.wwme.login.config.WebConfig;
import com.wwme.wwme.login.jwt.JWTUtil;
import com.wwme.wwme.login.service.NicknameService;
import com.wwme.wwme.user.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.MockBeans;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(NicknameController.class)
@Import({SecurityTestConfig.class, WebConfig.class})
public class NicknameControllerTest {
    @Autowired private MockMvc mvc;

    @MockBean
    private NicknameService nicknameService;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private JWTUtil jwtUtil;

    @Test
    @DisplayName("권한이 없는 유저가 /login/nickname으로 get 요청을 보낼 수 없음")
    public void unauthorizedUserCannotGetmapping() throws Exception {
        //given
        mvc.perform(MockMvcRequestBuilders.get("/login/nickname"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("http://localhost/login"));
    }

    @Test
    @DisplayName("권한이 없는 유저가 /login/nickname으로 post 요청을 보낼 수 없음")
    public void unauthorizedUserCannotPostmapping() throws Exception {
        //given
        mvc.perform(MockMvcRequestBuilders.post("/login/nickname"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("http://localhost/login"));
    }
}
