package com.estudo.bookie.services;

import com.estudo.bookie.entities.User;
import com.estudo.bookie.repositories.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
public class UserDetailServiceTest {

    @InjectMocks
    private UserDetailService userDetailService;

    @Mock
    private UserRepository userRepository;

    private User user;
    private String existingUsername = "User";
    private String nonExistingUsername = "notUser";

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setUsername(existingUsername);
        user.setPassword("123456");
    }

    @Test
    void loadUserByUsername_Should_Return_UserDetails_When_User_Exists() {
        when(userRepository.findByUsername(existingUsername)).thenReturn(Optional.of(user));

        UserDetails result = userDetailService.loadUserByUsername(existingUsername);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(existingUsername, result.getUsername());
        Assertions.assertEquals("123456", result.getPassword());

        verify(userRepository).findByUsername(existingUsername);
    }

    @Test
    void loadUserByUsername_Should_Throw_UsernameNotFoundException_When_User_Does_Not_Exist() {
        when(userRepository.findByUsername(nonExistingUsername)).thenReturn(Optional.empty());

        Assertions.assertThrows(UsernameNotFoundException.class, () -> {
            userDetailService.loadUserByUsername(nonExistingUsername);
        });

        verify(userRepository).findByUsername(nonExistingUsername);
    }
}