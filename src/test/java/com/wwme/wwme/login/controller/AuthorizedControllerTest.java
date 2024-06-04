package com.wwme.wwme.login.controller;

import com.wwme.wwme.login.config.SecurityTestConfig;
import com.wwme.wwme.login.config.WebConfig;
import com.wwme.wwme.login.jwt.JWTUtil;
import com.wwme.wwme.user.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = {AuthorizedController.class})
@Import({SecurityTestConfig.class, WebConfig.class})
public class AuthorizedControllerTest {
    @Autowired
    private MockMvc mvc;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private JWTUtil jwtUtil;

    @Test
    @DisplayName("권한을 받은 유저만이 /my 에 접근 가능함")
    @WithMockUser(username = "testUser", roles = "USER")
    public void getMappingMyPageTest() throws Exception {
        //given
        mvc.perform(MockMvcRequestBuilders.get("/my"))
                .andExpect(status().isOk())
                .andExpect(content().string("my route"));
    }

    @Test
    @DisplayName("권한을 받지 않은 유저는 /my에 접근하면 /login으로 리다이렉트")
    public void getMappingMyPageRedirectLoginPage() throws Exception {
        //given
        mvc.perform(MockMvcRequestBuilders.get("/my"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("http://localhost/login"));
    }
}
