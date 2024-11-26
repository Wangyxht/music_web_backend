package com.music.demo.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.music.demo.entity.Like;
import com.music.demo.utils.ThreadLocalUtil;
import org.springframework.stereotype.Service;

import java.util.Map;

public interface LikeService extends IService<Like> {

    void add(Long playlistId);

    void remove(Long playlistId);
}
