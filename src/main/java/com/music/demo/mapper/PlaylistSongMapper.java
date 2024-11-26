package com.music.demo.mapper;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.music.demo.entity.PlaylistSong;
import com.music.demo.vo.SongBaseVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface PlaylistSongMapper extends BaseMapper<PlaylistSong> {

    Page<SongBaseVO> selectPlaylistSongByPlaylistId(IPage<SongBaseVO> page,
                                                    @Param(Constants.WRAPPER) QueryWrapper<SongBaseVO> wrapper,
                                                    Long userId);
}
