package com.music.demo.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.music.demo.entity.Tags;
import com.music.demo.mapper.TagsMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class TagsServiceImpl extends ServiceImpl<TagsMapper, Tags> implements TagsService {

    @Autowired
    private TagsMapper tagsMapper;

    @Override
    public ArrayList<String> getTags(Long entityId, Integer type) {
        return tagsMapper.getTags(entityId, type);
    }

    @Override
    public void deleteTags(Long entityId, Integer type) {
        QueryWrapper<Tags> wrapper = new QueryWrapper<>();
        wrapper.eq("entity_id", entityId);
        wrapper.eq("type", type);

        tagsMapper.delete(wrapper);
    }
}
