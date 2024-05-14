package com.wwme.wwme.login.service;

import com.wwme.wwme.login.domain.entity.UserEntity;
import com.wwme.wwme.login.repository.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@WebMvcTest(controllers = NicknameService.class)
@ExtendWith(MockitoExtension.class)
public class NicknameServiceTest {
    @Autowired
    private NicknameService nicknameService;
    @MockBean
    private UserRepository userRepository;

    @Test
    @DisplayName("닉네임이 null이라면 IllegalArgumentException 발생")
    public void nicknameNullThrowIllegalArgumentException() throws Exception {
        //given
        UserEntity user = createUserEntity();

        when(userRepository.findByUserKey(any()))
                .thenReturn(user);

        Assertions.assertThrows(IllegalArgumentException.class,
                () -> nicknameService.saveNicknameAndChangeRole(null, "testUser"));
    }

    @Test
    @DisplayName("닉네임이 빈칸이라면 IllegalArgumentException 발생")
    public void nicknameBlankThrowIllegalArgumentException() throws Exception {
        //given
        UserEntity user = createUserEntity();

        when(userRepository.findByUserKey(any()))
                .thenReturn(user);

        Assertions.assertThrows(IllegalArgumentException.class,
                () -> nicknameService.saveNicknameAndChangeRole("", "testUser"));
    }

    @Test
    @DisplayName("닉네임이 올바르게 입력되면 닉네임을 저장, 권한 상승, 가입 날짜를 저장함")
    public void saveNicknameSuccessful() throws Exception {
        String nickname = "testUser";
        UserEntity userEntity = createUserEntity();
        when(userRepository.findByUserKey(any()))
                .thenReturn(userEntity);

        nicknameService.saveNicknameAndChangeRole(nickname, "testUserKey");

        assertThat(userEntity.getNickname()).isEqualTo(nickname);
        assertThat(userEntity.getRole()).isEqualTo("ROLE_USER");
        assertThat(userEntity.getRegistrationDate()).isNotNull();
    }


    private static UserEntity createUserEntity() {
        UserEntity user = new UserEntity();
        user.setId(1L);
        user.setName("testUser");
        user.setNickname(null);
        user.setUserKey("testUser");
        user.setRole("ROLE_TEMP");
        user.setRegistrationDate(null);
        user.setSocialProvider("naver");
        return user;
    }

}
