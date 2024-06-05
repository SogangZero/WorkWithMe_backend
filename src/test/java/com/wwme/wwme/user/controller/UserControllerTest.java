package com.wwme.wwme.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wwme.wwme.login.config.SecurityTestConfig;
import com.wwme.wwme.login.config.WebConfig;
import com.wwme.wwme.login.domain.dto.UserDTO;
import com.wwme.wwme.login.jwt.JWTUtil;
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
import org.springframework.boot.test.mock.mockito.MockBeans;
import org.springframework.context.annotation.Import;
import org.springframework.core.MethodParameter;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.NoSuchElementException;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@Import({SecurityTestConfig.class, WebConfig.class})
@WithMockUser(username = "testUser", roles = "USER")
public class UserControllerTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mvc;

    @MockBean
    private JWTUtil jwtUtil;

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
        UserInfoDTO userDTO = new UserInfoDTO(true, nickname, 0);
        String jsonUser = objectMapper.writeValueAsString(userDTO);


        //when
        when(jwtUtil.getUserKey(any()))
                .thenReturn("testUserKey");
        when(userRepository.findByUserKey(any()))
                .thenReturn(Optional.of(user));

        //then
        mvc.perform(
                        MockMvcRequestBuilders.get("/user"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(content().string(jsonUser));
    }

    @Test
    @DisplayName("유저가 존재하지 않을 때 - /user [GET]")
    public void requestInvalidUser() throws Exception {
        //given
        String accessToken = "testToken";
        User emptyUser = new User();
        emptyUser.setId(-1L);

        //when
        when(jwtUtil.getUserKey(any()))
                .thenReturn("testUserKey");
        when(userRepository.findByUserKey(any()))
                .thenReturn(Optional.of(emptyUser));

        //then
        mvc.perform(
                        MockMvcRequestBuilders.get("/user"))
                .andExpect(status().isBadRequest());
    }


}
