package com.estudo.bookie.services;

import com.estudo.bookie.entities.User;

import com.estudo.bookie.entities.dtos.UserRequestDto;
import com.estudo.bookie.entities.dtos.UserResponseDto;
import com.estudo.bookie.factory.UserFactory;
import com.estudo.bookie.repositories.UserRepository;

import com.estudo.bookie.services.exceptions.ResourceNotFound;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;


import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @InjectMocks
    private UserService userService;

    Long existingId = 1L;
    Long nonExistingId = 99L;
    User user = UserFactory.createUser();
    List<User> users = List.of(user);

    @Test
    public void find_All_Should_Return_List_Of_Users(){
        Mockito.when(userRepository.findAll()).thenReturn(users);

        List<UserResponseDto> result = userService.findAll();
        Assertions.assertNotNull(result);
        Assertions.assertEquals(users.size(),result.size());
        Assertions.assertEquals("Test", result.get(0).username());
    }

    @Test
    public void find_By_Id_Should_Return_User(){
        Mockito.when(userRepository.findById(existingId)).thenReturn(Optional.of(user));
        UserResponseDto result = userService.findById(existingId);
        Assertions.assertNotNull(result);
        Assertions.assertEquals("Test", result.username());
        verify(userRepository).findById(existingId);
    }

    @Test
    public void find_By_Id_Should_Return_ResourceNotFound_when_id_not_found(){
        Mockito.when(userRepository.findById(nonExistingId)).thenReturn(Optional.empty());
        Assertions.assertThrows(ResourceNotFound.class, () -> userService.findById(nonExistingId));
        verify(userRepository).findById(nonExistingId);
    }

    @Test
    public void save_Should_Create_User_with_encoded_password(){
        UserRequestDto userRequestDto = new UserRequestDto(1L, "test", "test@mail.com", "123456");

        when(bCryptPasswordEncoder.encode("123456")).thenReturn("encodedpass");
        Mockito.when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        UserResponseDto result = userService.save(userRequestDto);
        Assertions.assertNotNull(result);
        Assertions.assertEquals("test", result.username());
        Assertions.assertEquals("test@mail.com", result.email());

        verify(userRepository).save(any(User.class));
        verify(bCryptPasswordEncoder).encode("123456");
    }

    @Test
    public void update_Should_Update_User_with_encoded_password(){
        UserRequestDto userRequestDto = new UserRequestDto(existingId, "updated", "newEmail", "newPassword");

        when(userRepository.findById(existingId)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(bCryptPasswordEncoder.encode("newPassword")).thenReturn("encodedpass");

        UserResponseDto result = userService.update(existingId, userRequestDto);

        Assertions.assertNotNull(result);
        Assertions.assertEquals("updated", result.username());
        Assertions.assertEquals("newEmail", result.email());

        verify(userRepository).save(any(User.class));
        verify(bCryptPasswordEncoder).encode("newPassword");
    }

    @Test
    public void update_should_return_resourceNotFound_when_id_not_found(){
        UserRequestDto userRequestDto = new UserRequestDto(1L, "test", "test@mail.com", "123456");
        when(userRepository.findById(nonExistingId)).thenReturn(Optional.empty());
        Assertions.assertThrows(ResourceNotFound.class, () -> {
            userService.update(nonExistingId, userRequestDto);
        });
        verify(userRepository).findById(nonExistingId);
    }

    @Test
    void delete_Should_Call_Method_Once(){
        when(userRepository.existsById(existingId)).thenReturn(true);
        userService.delete(existingId);
        verify(userRepository).deleteById(existingId);
    }

    @Test
    void delete_Should_Throw_ResourceNotFound_When_Id_Does_Not_Exist() {
        when(userRepository.existsById(nonExistingId)).thenReturn(false);
        Assertions.assertThrows(ResourceNotFound.class, () -> userService.delete(nonExistingId));
    }

}
