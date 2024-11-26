package com.music.demo.mapstruct;

import com.music.demo.dto.UserRegisterDTO;
import com.music.demo.dto.UserUpdateDTO;
import com.music.demo.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface UserDtoMapper {

    UserDtoMapper INSTANCE = Mappers.getMapper(UserDtoMapper.class);

    // DTO 转实体
    User toEntity(UserRegisterDTO userRegisterDTO);

    User toEntity(UserUpdateDTO updateDTO);

}
