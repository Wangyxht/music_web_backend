package com.music.demo.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.music.demo.entity.Tags;

import java.util.ArrayList;

public interface TagsService extends IService<Tags> {

    ArrayList<String> getTags(Long entityId, Integer type);

    void deleteTags(Long entityId, Integer type);
}
