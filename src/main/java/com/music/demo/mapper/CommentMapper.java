package com.music.demo.mapper;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.music.demo.entity.Comment;
import com.music.demo.vo.CommentVO;
import com.music.demo.vo.PlaylistBaseVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface CommentMapper extends BaseMapper<Comment> {

    Page<CommentVO> selectCommentPage(Page<CommentVO> page, @Param(Constants.WRAPPER) QueryWrapper<CommentVO> wrapper);

}
