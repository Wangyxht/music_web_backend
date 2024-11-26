package com.music.demo.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.music.demo.dto.*;
import com.music.demo.entity.Playlist;
import com.music.demo.vo.PlaylistBaseVO;
import com.music.demo.vo.PlaylistDetailVO;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Service;

import java.util.List;

public interface PlaylistService extends IService<Playlist> {

    void create(PlayListUploadDTO playlistUploadDTO);

    void delete(Long id);

    Page<PlaylistBaseVO> search(PlaylistSearchDTO playlistSearchDTO);

    Page<PlaylistBaseVO> searchByUser(PlaylistUserSearchDTO playlistUserSearchDTO);

    PlaylistDetailVO getPlaylistDetail(Long playlistId);

    void getCover(HttpServletResponse response, Long aLong);

    void update(PlaylistUpdateDTO playlistUpdateDTO);

    Page<Playlist> adminSearch(PlaylistSearchDTO playlistSearchDTO);

    List<PlaylistBaseVO> recommend(Integer playlistNum);
}
