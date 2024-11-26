package com.music.demo.mapstruct;

import com.music.demo.dto.PlayListUploadDTO;
import com.music.demo.dto.PlaylistUpdateDTO;
import com.music.demo.dto.SongUploadDTO;
import com.music.demo.entity.Playlist;
import com.music.demo.entity.Song;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface PlaylistDtoMapper {

    PlaylistDtoMapper INSTANCE = Mappers.getMapper(PlaylistDtoMapper.class);

    Playlist toEntity(PlayListUploadDTO playListUploadDTO);

    Playlist toEntity(PlaylistUpdateDTO playListUpdateDTO);

}
