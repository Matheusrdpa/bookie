package com.estudo.bookie.services;

import com.estudo.bookie.entities.User;
import com.estudo.bookie.entities.dtos.UserRequestDto;
import com.estudo.bookie.entities.dtos.UserResponseDto;
import com.estudo.bookie.repositories.UserRepository;
import com.estudo.bookie.services.exceptions.ResourceNotFound;
import com.estudo.bookie.services.mappers.UserMapper;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserService {
    private UserRepository userRepository;
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    public UserService(UserRepository userRepository, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.userRepository = userRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    @Transactional(readOnly = true)
    public List<UserResponseDto> findAll() {
        List<User> users = userRepository.findAll();
        List<UserResponseDto> userResponseDtos = users.stream().map(UserMapper.INSTANCE::toUserResponseDto).toList();
        return userResponseDtos;
    }

    @Transactional(readOnly = true)
    public UserResponseDto findById(Long id) {
        User user = userRepository.findById(id).orElseThrow(() -> new ResourceNotFound("User not found"));
        return UserMapper.INSTANCE.toUserResponseDto(user);
    }

    @Transactional
    public UserResponseDto save(UserRequestDto userRequestDto) {
        User user = UserMapper.INSTANCE.requestToUser(userRequestDto);
        user.setPassword(userRequestDto.password());
        user = userRepository.save(user);
        return UserMapper.INSTANCE.toUserResponseDto(user);
    }

    @Transactional
    public UserResponseDto update(Long id, UserRequestDto userRequestDto) {
        User user = userRepository.findById(id).orElseThrow(() -> new ResourceNotFound("User not found"));
        user.setEmail(user.getEmail());
        user.setUsername(user.getUsername());
        user.setPassword(user.getPassword());
        user = userRepository.save(user);
        return UserMapper.INSTANCE.toUserResponseDto(user);
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    public void delete(Long id) {
        if(!userRepository.existsById(id)) {
            throw new ResourceNotFound("User not found");
        }
        userRepository.deleteById(id);
    }
}
