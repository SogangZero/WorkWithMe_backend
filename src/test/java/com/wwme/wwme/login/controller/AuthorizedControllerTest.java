package com.wwme.wwme.login.controller;

import com.wwme.wwme.login.config.SecurityTestConfig;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = {AuthorizedController.class})
@Import(SecurityTestConfig.class)
public class AuthorizedControllerTest {
    @Autowired
    private MockMvc mvc;

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
