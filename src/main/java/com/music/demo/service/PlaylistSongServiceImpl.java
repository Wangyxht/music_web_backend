package com.music.demo.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.music.demo.dto.PlaylistSongSearchDTO;
import com.music.demo.entity.PlaylistSong;
import com.music.demo.mapper.PlaylistSongMapper;
import com.music.demo.utils.ThreadLocalUtil;
import com.music.demo.vo.SongBaseVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class PlaylistSongServiceImpl extends ServiceImpl<PlaylistSongMapper, PlaylistSong> implements PlaylistSongService {

    @Autowired
    private PlaylistSongMapper playlistSongMapper;


    @Override
    public void addSong(PlaylistSong playlistSong){
        QueryWrapper<PlaylistSong> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("playlist_id", playlistSong.getPlaylistId());
        queryWrapper.eq("song_id", playlistSong.getSongId());
        if(playlistSongMapper.exists(queryWrapper)){
            throw new IllegalArgumentException("歌曲被重复添加。");
        }

        if(playlistSongMapper.insert(playlistSong) == 0){
            throw new IllegalArgumentException("歌曲添加失败。");
        }
    }

    @Override
    public void removeSong(PlaylistSong playlistSong){
        QueryWrapper<PlaylistSong> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("playlist_id", playlistSong.getPlaylistId());
        queryWrapper.eq("song_id", playlistSong.getSongId());
        if(!remove(queryWrapper)){
            throw new RuntimeException("歌曲移除失败。");
        }
    }

    @Override
    public Page<SongBaseVO> listSongs(PlaylistSongSearchDTO playlistSongSearchDTO) {
        Map<String, Object> map = (Map<String, Object>) ThreadLocalUtil.get();
        Long userId = (Long) map.get("id");

        Page<SongBaseVO> page = new Page<>(playlistSongSearchDTO.getPage(), playlistSongSearchDTO.getLimit());
        QueryWrapper<SongBaseVO> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("pls.playlist_id", playlistSongSearchDTO.getPlaylistId());
        queryWrapper.eq("s.is_delete", false);

        return playlistSongMapper.selectPlaylistSongByPlaylistId(page, queryWrapper, userId);
    }
}
