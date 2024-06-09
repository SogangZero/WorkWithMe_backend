package com.wwme.wwme.user.repository;

import com.wwme.wwme.user.domain.User;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;

@SpringBootTest
public class UserTest {

    private UserRepository userRepository;

    @Autowired
    public UserTest(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Test
    void insertUser(){
        User user = new User();
        user.setRegisterDate(LocalDateTime.now());
        user.setNickname("dohkun");
        user.setSocialProvider("whatissocialprovider?");

        User findUser = userRepository.save(user);



        Assertions.assertThat(findUser.getId()).isEqualTo(user.getId());

    }
}
