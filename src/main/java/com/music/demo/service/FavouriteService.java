package com.music.demo.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.music.demo.dto.FavouriteSearchDTO;
import com.music.demo.entity.Favourite;
import com.music.demo.enums.FavouriteTypeEnum;
import com.music.demo.vo.PlaylistBaseVO;
import com.music.demo.vo.SongBaseVO;
import org.springframework.stereotype.Service;

public interface FavouriteService extends IService<Favourite> {

    void add(Long id, Integer type);

    void delete(Long id, Integer type);

    Page<SongBaseVO> getFavouriteSong(FavouriteSearchDTO favouriteSearchDTO);

    Page<PlaylistBaseVO> getFavouritePlayList(FavouriteSearchDTO favouriteSearchDTO);

}
