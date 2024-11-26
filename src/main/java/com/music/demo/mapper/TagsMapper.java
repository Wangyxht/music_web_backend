package com.music.demo.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.music.demo.entity.Tags;

import java.util.ArrayList;

public interface TagsMapper extends BaseMapper<Tags> {

    ArrayList<String> getTags(Long entityId, Integer type);
}
