package com.music.demo.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.music.demo.dto.PlaylistSongSearchDTO;
import com.music.demo.entity.PlaylistSong;
import com.music.demo.vo.SongBaseVO;
import org.springframework.stereotype.Service;

public interface PlaylistSongService extends IService<PlaylistSong> {

    void addSong(PlaylistSong playlistSong);

    void removeSong(PlaylistSong playlistSong);

    Page<SongBaseVO> listSongs(PlaylistSongSearchDTO playlistSongSearchDTO);
}
