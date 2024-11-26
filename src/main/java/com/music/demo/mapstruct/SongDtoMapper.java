package com.music.demo.mapstruct;

import com.music.demo.dto.SongUploadDTO;
import com.music.demo.entity.Song;
import com.music.demo.vo.SongBaseVO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface SongDtoMapper {

    SongDtoMapper INSTANCE = Mappers.getMapper(SongDtoMapper.class);

    Song toEntity(SongUploadDTO songUploadDTO);

    SongBaseVO toBaseVO(Song song);

    List<SongBaseVO> toBaseVOList(List<Song> songs);
}
