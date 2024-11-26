package com.music.demo.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.music.demo.dto.FavouriteSearchDTO;
import com.music.demo.entity.Favourite;
import com.music.demo.entity.User;
import com.music.demo.enums.FavouriteTypeEnum;
import com.music.demo.mapper.FavouriteMapper;
import com.music.demo.utils.ThreadLocalUtil;
import com.music.demo.vo.PlaylistBaseVO;
import com.music.demo.vo.SongBaseVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;

@Service
public class FavouriteServiceImpl extends ServiceImpl<FavouriteMapper, Favourite> implements FavouriteService {

    @Autowired
    private FavouriteMapper favouriteMapper;

    @Override
    public void add(Long id, Integer type){

        Map<String, Object> map = (Map<String, Object>) ThreadLocalUtil.get();
        Long userId = (Long) map.get("id");

        Favourite favourite = new Favourite();
        favourite.setEntityId(id);
        favourite.setUserId(userId);
        favourite.setFavouriteType(type.equals(0) ? FavouriteTypeEnum.SONG_FAVOURITE : FavouriteTypeEnum.PLAYLIST_FAVOURITE);
        favourite.setFavouriteTime(LocalDateTime.now());

        if(!this.save(favourite)){
            throw new RuntimeException("收藏错误");
        }
    }

    @Override
    public void delete(Long id, Integer type) {
        Map<String, Object> map = (Map<String, Object>) ThreadLocalUtil.get();
        Long userId = (Long) map.get("id");

        QueryWrapper<Favourite> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", userId);
        wrapper.eq("entity_id", id);
        wrapper.eq("favourite_type", type);

        if(!this.remove(wrapper)){
            throw new RuntimeException("取消收藏错误");
        }
    }

    @Override
    public Page<SongBaseVO> getFavouriteSong(FavouriteSearchDTO favouriteSearchDTO) {
        Page<SongBaseVO> page = new Page<>(favouriteSearchDTO.getPage(), favouriteSearchDTO.getLimit());
        page.setOptimizeCountSql(false);

        Map<String, Object> map = (Map<String, Object>) ThreadLocalUtil.get();
        Long userId = (Long) map.get("id");

        QueryWrapper<SongBaseVO> wrapper = new QueryWrapper<>();
        wrapper.eq("favourite_type", 0);
        wrapper.eq("user_id", favouriteSearchDTO.getUserId());
        wrapper.eq("s.is_delete", false);

        return favouriteMapper.selectFavouriteSongsPage(page, wrapper, userId);
    }

    @Override
    public Page<PlaylistBaseVO> getFavouritePlayList(FavouriteSearchDTO favouriteSearchDTO) {
        Page<PlaylistBaseVO> page = new Page<>(favouriteSearchDTO.getPage(), favouriteSearchDTO.getLimit());
        page.setOptimizeCountSql(false);

        QueryWrapper<PlaylistBaseVO> wrapper = new QueryWrapper<>();
        wrapper.eq("favourite_type", 1);
        wrapper.eq("f.user_id", favouriteSearchDTO.getUserId());
        wrapper.eq("pl.is_delete", false);

        return favouriteMapper.selectFavouritePlaylistsPage(page, wrapper);
    }

}
