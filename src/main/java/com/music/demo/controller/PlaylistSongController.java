package com.music.demo.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.music.demo.dto.PlaylistSongSearchDTO;
import com.music.demo.entity.PlaylistSong;
import com.music.demo.service.PlaylistSongService;
import com.music.demo.utils.ApiResponse;
import com.music.demo.vo.SongBaseVO;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/playlist/")
@CrossOrigin("*")
public class PlaylistSongController {

    @Autowired
    PlaylistSongService playlistSongService;

    @PostMapping("add-song")
    ResponseEntity<Object> addSong(@NotNull @RequestBody PlaylistSong playlistSong){
        if(playlistSong.getSongId() == null || playlistSong.getPlaylistId() == null){
            return ResponseEntity
                    .internalServerError()
                    .body(ApiResponse.success("请求添加歌曲参数非法。"));
        }

        try{
            playlistSongService.addSong(playlistSong);
            return ResponseEntity
                    .ok()
                    .body(ApiResponse.success("歌曲添加成功"));
        } catch (Exception e){
            return ResponseEntity
                    .internalServerError()
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    @DeleteMapping("remove-song")
    ResponseEntity<Object> deleteSong(@NotNull @ModelAttribute PlaylistSong playlistSong){
        if(playlistSong.getSongId() == null || playlistSong.getPlaylistId() == null){
            return ResponseEntity
                    .internalServerError()
                    .body(ApiResponse.success("请求添加歌曲参数非法。"));
        }

        try{
            playlistSongService.removeSong(playlistSong);
            return ResponseEntity
                    .ok()
                    .body(ApiResponse.success("歌曲添加成功"));
        } catch (Exception e){
            return ResponseEntity
                    .internalServerError()
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("list-song")
    ResponseEntity<Object> listSong(@Validated @ModelAttribute PlaylistSongSearchDTO playlistSongSearchDTO) {
        try{
            Page<SongBaseVO> page = playlistSongService.listSongs(playlistSongSearchDTO);
            return ResponseEntity
                    .ok()
                    .body(ApiResponse.success(page, "歌曲查询成功"));
        } catch (Exception e){
            return ResponseEntity
                    .internalServerError()
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

}
