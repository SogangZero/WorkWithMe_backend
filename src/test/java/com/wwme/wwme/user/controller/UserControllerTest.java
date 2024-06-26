package com.wwme.wwme.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wwme.wwme.login.config.SecurityTestConfig;
import com.wwme.wwme.login.config.ResolverConfig;
import com.wwme.wwme.login.domain.dto.response.DataDTO;
import com.wwme.wwme.login.service.JWTUtilService;
import com.wwme.wwme.user.domain.User;
import com.wwme.wwme.user.domain.dto.UserInfoDTO;
import com.wwme.wwme.user.repository.UserRepository;
import com.wwme.wwme.user.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@Import({SecurityTestConfig.class, ResolverConfig.class})
@WithMockUser(username = "testUser", roles = "USER")
public class UserControllerTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mvc;

    @MockBean
    private JWTUtilService jwtUtilService;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private UserService userService;

    @Mock
    private HttpServletRequest request;

    @Test
    @DisplayName("user가 존재할 때 - /user [GET]")
    public void requestValidUserInfo() throws Exception {
        //given
        String nickname = "testUser";
        User user = new User();
        user.setId(0L);
        user.setNickname(nickname);
        UserInfoDTO userDTO = new UserInfoDTO(true, nickname, 0L);
        DataDTO result = new DataDTO(userDTO);
        String jsonUser = objectMapper.writeValueAsString(result);


        //when
        when(jwtUtilService.getUserKey(any()))
                .thenReturn("testUserKey");
        when(userRepository.findByUserKey(any()))
                .thenReturn(Optional.of(user));
        when(userService.getUserInfo(any()))
                .thenReturn(userDTO);

        //then
        mvc.perform(
                        MockMvcRequestBuilders.get("/user"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(content().string(jsonUser));

    }
}
