package com.music.demo.mapper;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.music.demo.entity.Favourite;
import com.music.demo.vo.PlaylistBaseVO;
import com.music.demo.vo.SongBaseVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface FavouriteMapper extends BaseMapper<Favourite> {

    /**
     * 获取某一用户的歌曲收藏
     * @param page
     * @param wrapper
     * @return 数据库查询结果
     */
    Page<SongBaseVO> selectFavouriteSongsPage(IPage<SongBaseVO> page,
                                              @Param(Constants.WRAPPER) QueryWrapper<SongBaseVO> wrapper,
                                              Long userId);

    /**
     * 获取某一用户的歌单收藏
     * @param page
     * @param wrapper
     * @return
     */
    Page<PlaylistBaseVO> selectFavouritePlaylistsPage(IPage<PlaylistBaseVO> page,
                                                      @Param(Constants.WRAPPER) QueryWrapper<PlaylistBaseVO> wrapper);
}
