package com.estudo.bookie.repositories;

import com.estudo.bookie.entities.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

@DataJpaTest
@ActiveProfiles("test")
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    void findByUsername_Should_Return_User_When_Exists() {
        User user = new User();
        user.setEmail("emailtest@m");
        user.setUsername("user_test");
        user.setPassword("123456");
        userRepository.save(user);


        Optional<User> result = userRepository.findByUsername("user_test");


        Assertions.assertTrue(result.isPresent());
        Assertions.assertEquals("user_test", result.get().getUsername());
    }

    @Test
    void findByUsername_Should_Return_Empty_When_Not_Exists() {
        Optional<User> result = userRepository.findByUsername("Doesn't-exist");
        Assertions.assertTrue(result.isEmpty());
    }
}