package com.music.demo.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.music.demo.dto.*;
import com.music.demo.entity.Song;
import com.music.demo.vo.SongBaseVO;
import com.music.demo.vo.SongDetailVO;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.transaction.annotation.Transactional;

import java.io.FileNotFoundException;
import java.util.List;

public interface SongService extends IService<Song> {

    Page<SongBaseVO> search(SongSearchDTO songSearchDTO);

    Page<SongBaseVO> searchByUser(SongUserSearchDTO songUserSearchDTO);

    SongDetailVO getSongDetail(Long id) throws FileNotFoundException;

    void upload(SongUploadDTO songUploadDTO);

    void getCover(HttpServletResponse response, Long songId);

    void audit(Long id, Boolean audit);

    Page<Song> getAuditList(PageSearchDTO pageSearchDTO);

    void delete(Long songId);

    @Transactional(rollbackFor = Exception.class)
    void update(SongUpdateDTO songUpdateDTO);

    Page<Song> adminSearch(SongSearchDTO songSearchDTO);

    List<SongBaseVO> recommend(Integer songNum);
}
