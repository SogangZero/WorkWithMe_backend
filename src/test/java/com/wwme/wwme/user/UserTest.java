package com.wwme.wwme.user;

import com.wwme.wwme.user.domain.User;
import com.wwme.wwme.user.repository.UserRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.Optional;

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
        user.setRegister_date(LocalDateTime.now());
        user.setNickname("dohkun");
        user.setSocial_provider("whatissocialprovider?");
        user.setId(1L);

        userRepository.save(user);

        Optional<User> findUserOp = userRepository.findById(1L);

        if(findUserOp.isPresent()){
            Assertions.assertThat(findUserOp.get().getId()).isEqualTo(user.getId());
        }else{
            Assertions.fail("could not find User from DB`");
        }
    }
}
