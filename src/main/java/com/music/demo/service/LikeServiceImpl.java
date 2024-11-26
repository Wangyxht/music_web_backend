package com.music.demo.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.music.demo.entity.Like;
import com.music.demo.mapper.LikeMapper;
import com.music.demo.utils.ThreadLocalUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;

@Service
public class LikeServiceImpl extends ServiceImpl<LikeMapper, Like> implements LikeService {

    @Autowired
    private LikeMapper likeMapper;

    @Override
    public void add(Long playlistId){
        Map<String, Object> map = (Map<String, Object>) ThreadLocalUtil.get();
        Long userId = (Long) map.get("id");

        Like like = new Like();
        like.setPlaylistId(playlistId);
        like.setUserId(userId);
        like.setLikeTime(LocalDateTime.now());

        if(likeMapper.insert(like) == 0){
            throw new RuntimeException("点赞失败。");
        }
    }

    @Override
    public void remove(Long playlistId){
        Map<String, Object> map = (Map<String, Object>) ThreadLocalUtil.get();
        Long userId = (Long) map.get("id");

        Like like = new Like();
        like.setPlaylistId(playlistId);
        like.setUserId(userId);

        QueryWrapper<Like> queryWrapper = new QueryWrapper<Like>();
        queryWrapper.eq("playlist_id", playlistId);
        queryWrapper.eq("user_id", userId);

        if(likeMapper.delete(queryWrapper) == 0){
            throw new RuntimeException("取消点赞失败。");
        }
    }

}
