package com.music.demo.mapper;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.music.demo.entity.Playlist;
import com.music.demo.vo.PlaylistBaseVO;
import com.music.demo.vo.PlaylistDetailVO;
import com.music.demo.vo.SongBaseVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface PlaylistMapper extends BaseMapper<Playlist> {

    Page<PlaylistBaseVO> selectPlaylistPage(Page<PlaylistBaseVO> page, @Param(Constants.WRAPPER) QueryWrapper<PlaylistBaseVO> wrapper);

    PlaylistDetailVO selectPlaylistDetail(Long playlistId, Long userId);

    List<PlaylistBaseVO> selectTopPlaylists(Integer limit);
}
