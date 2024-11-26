package com.music.demo.mapstruct;

import com.music.demo.dto.CommentCreateDTO;
import com.music.demo.entity.Comment;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface CommentDtoMapper {

    CommentDtoMapper INSTANCE = Mappers.getMapper(CommentDtoMapper.class);

    Comment toEntity(CommentCreateDTO commentCreateDTO);
}
