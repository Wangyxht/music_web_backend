package com.music.demo.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.music.demo.dto.CommentCreateDTO;
import com.music.demo.dto.CommentSearchDTO;
import com.music.demo.dto.PageSearchDTO;
import com.music.demo.entity.Comment;
import com.music.demo.vo.CommentVO;

public interface CommentService extends IService<Comment> {

    void create(CommentCreateDTO commentCreateDTO);

    void delete(Long commentId);

    void audit(Long id, Boolean audit);

    Page<CommentVO> searchComment(CommentSearchDTO commentSearchDTO, Integer commentType);

    Page<CommentVO> searchCommentByUser(CommentSearchDTO commentSearchDTO);

    Page<CommentVO> getAuditList(PageSearchDTO pageSearchDTO);
}
