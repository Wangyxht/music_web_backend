package com.music.demo.mapper;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.music.demo.entity.Song;
import com.music.demo.vo.SongBaseVO;
import com.music.demo.vo.SongDetailVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface SongMapper extends BaseMapper<Song> {

    Page<SongBaseVO> selectSongPage(IPage<SongBaseVO> page, @Param(Constants.WRAPPER) QueryWrapper<SongBaseVO> wrapper, Long userId);

    SongDetailVO selectSongDetail(@Param(Constants.WRAPPER) QueryWrapper<SongDetailVO> wrapper, Long userId);

    List<SongBaseVO> selectTopSongs(Integer limit, Long userId);
}
