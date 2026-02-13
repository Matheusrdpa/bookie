package com.estudo.bookie.services.mappers;

import com.estudo.bookie.entities.User;
import com.estudo.bookie.entities.dtos.UserRequestDto;
import com.estudo.bookie.entities.dtos.UserResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface UserMapper {

    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    UserResponseDto toUserResponseDto(User user);
    UserRequestDto toUserRequestDto(User user);
    User requestToUser(UserRequestDto userRequestDto);

}
