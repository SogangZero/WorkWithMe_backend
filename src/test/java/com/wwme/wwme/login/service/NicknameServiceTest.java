//package com.wwme.wwme.login.service;
//
//import com.wwme.wwme.user.domain.User;
//import com.wwme.wwme.user.repository.UserRepository;
//import org.junit.jupiter.api.Assertions;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
//import org.springframework.boot.test.mock.mockito.MockBean;
//
//import java.util.Optional;
//
//import static org.assertj.core.api.Assertions.assertThat;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.Mockito.when;
//
//@ExtendWith(MockitoExtension.class)
//@WebMvcTest(value = NicknameService.class)
//public class NicknameServiceTest {
//    @Autowired
//    private NicknameService nicknameService;
//    @MockBean
//    private UserRepository userRepository;
//    @MockBean
//    private JWTUtilService jwtUtilService;
//
//    @Test
//    @DisplayName("닉네임이 null이라면 IllegalArgumentException 발생")
//    public void nicknameNullThrowIllegalArgumentException() throws Exception {
//        //given
//        User user = createUserEntity();
//
//        when(userRepository.findByUserKey(any()))
//                .thenReturn(Optional.of(user));
//
//        Assertions.assertThrows(IllegalArgumentException.class,
//                () -> nicknameService.saveNickname(null, "testUser", profileImage));
//    }
//
//    @Test
//    @DisplayName("닉네임이 빈칸이라면 IllegalArgumentException 발생")
//    public void nicknameBlankThrowIllegalArgumentException() throws Exception {
//        //given
//        User user = createUserEntity();
//
//        when(userRepository.findByUserKey(any()))
//                .thenReturn(Optional.of(user));
//
//        Assertions.assertThrows(IllegalArgumentException.class,
//                () -> nicknameService.saveNickname("","testUser", profileImage));
//    }
//
//    @Test
//    @DisplayName("닉네임이 올바르게 입력되면 닉네임을 저장, 권한 상승, 가입 날짜를 저장함")
//    public void saveNicknameSuccessful() throws Exception {
//        String nickname = "testUser";
//        User user = createUserEntity();
//        when(userRepository.findByUserKey(any()))
//                .thenReturn(Optional.of(user));
//
//        nicknameService.saveNickname(nickname, "testUserKey", profileImage);
//
//        assertThat(user.getNickname()).isEqualTo(nickname);
//        assertThat(user.getRole()).isEqualTo("ROLE_USER");
//        assertThat(user.getRegisterDate()).isNotNull();
//    }
//
//
//    private static User createUserEntity() {
//        User user = new User();
//        user.setId(1L);
////        user.setName("testUser");
//        user.setNickname(null);
//        user.setUserKey("testUser");
//        user.setRole("ROLE_USER");
//        user.setRegisterDate(null);
//        user.setSocialProvider("naver");
//        return user;
//    }
//
//}
