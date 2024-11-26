package com.music.demo.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.music.demo.dto.CommentCreateDTO;
import com.music.demo.dto.CommentSearchDTO;
import com.music.demo.dto.PageSearchDTO;
import com.music.demo.entity.Comment;
import com.music.demo.mapper.CommentMapper;
import com.music.demo.mapper.PlaylistMapper;
import com.music.demo.mapper.SongMapper;
import com.music.demo.mapstruct.CommentDtoMapper;
import com.music.demo.utils.TextAuditUtil;
import com.music.demo.utils.ThreadLocalUtil;
import com.music.demo.vo.CommentVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Map;

@Service
public class CommentServiceImpl extends ServiceImpl<CommentMapper, Comment> implements CommentService {

    @Autowired
    private CommentMapper commentMapper;

    @Autowired
    private SongMapper songMapper;

    @Autowired
    private PlaylistMapper playlistMapper;

    @Autowired
    private TextAuditUtil textAuditUtil;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void create(CommentCreateDTO commentCreateDTO) {

        Map<String, Object> map = (Map<String, Object>) ThreadLocalUtil.get();
        Long userId = (Long) map.get("id");

        // 检查评论对象是否存在
        if(commentCreateDTO.getCommentType().equals(0)
                && songMapper.selectById(commentCreateDTO.getEntityId()) == null){
            throw new IllegalArgumentException("评论对象不存在");
        }
        else if(commentCreateDTO.getCommentType().equals(1)
                && playlistMapper.selectById(commentCreateDTO.getEntityId()) == null){
            throw new IllegalArgumentException("评论对象不存在");
        }

        // 自动审核
        Map<String, String> auditResult = textAuditUtil.auditText(
                commentCreateDTO.getContent(),
                "comment_detection"
        );

        // 新建评论
        Comment comment = CommentDtoMapper.INSTANCE.toEntity(commentCreateDTO);
        if(auditResult.get("riskLevel") != null)
            switch (auditResult.get("riskLevel")) {
                case "high", "medium" -> throw new IllegalArgumentException("评论涉嫌违规");
                case "low" -> comment.setAudit(false);
            }
        else comment.setAudit(true);
        comment.setUserId(userId);
        comment.setIsDelete(false);
        comment.setCommentTime(LocalDateTime.now());

        if(commentMapper.insert(comment) == 0) {
            throw new RuntimeException("评论创建失败。");
        }
    }

    @Override
    public void delete(Long commentId) {
        Map<String, Object> map = (Map<String, Object>) ThreadLocalUtil.get();
        Integer type = (Integer) map.get("type");
        Long userId = (Long) map.get("id");

        Comment comment = commentMapper.selectById(commentId);
        if(comment == null){
            throw new IllegalArgumentException("评论ID错误。");
        }
        else if(!type.equals(0) && !comment.getUserId().equals(userId)){
            throw new IllegalArgumentException("无删除权限。");
        }

        if(commentMapper.deleteById(commentId) == 0) {
            throw new RuntimeException("评论删除失败");
        }
    }

    @Override
    public void audit(Long id, Boolean audit) {
        // 鉴权
        Map<String, Object> userInfo = (Map<String, Object>) ThreadLocalUtil.get();
        Integer userType = (Integer) userInfo.get("type");

        // 检查用户权限是否为管理员
        if (!userType.equals(0)) {
            throw new IllegalArgumentException("无管理员权限！");
        }

        // 审核操作
        if (audit) {
            // 审核通过，更新评论状态为审核通过
            UpdateWrapper<Comment> updateWrapper = new UpdateWrapper<>();
            updateWrapper.lambda()
                    .eq(Comment::getId, id)
                    .eq(Comment::getAudit, false)
                    .set(Comment::getAudit, true);
            if (commentMapper.update(null, updateWrapper) == 0) {
                throw new RuntimeException("评论审核更新失败");
            }
        } else {
            // 审核不通过，删除评论
            if (!this.removeById(id)) {
                throw new RuntimeException("评论删除失败");
            }
        }
    }

    @Override
    public Page<CommentVO> searchComment(CommentSearchDTO commentSearchDTO, Integer commentType){
        // 检查评论对象是否存在
        if(commentType.equals(0)
                && songMapper.selectById(commentSearchDTO.getId()) == null){
            throw new IllegalArgumentException("评论对象不存在");
        }
        else if(commentType.equals(1)
                && playlistMapper.selectById(commentSearchDTO.getId()) == null){
            throw new IllegalArgumentException("评论对象不存在");
        }

        Page<CommentVO> page = new Page<>(commentSearchDTO.getPage(), commentSearchDTO.getLimit());
        page.setOptimizeCountSql(false);

        QueryWrapper<CommentVO> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("entity_id", commentSearchDTO.getId());
        queryWrapper.eq("c.is_delete", false);
        return commentMapper.selectCommentPage(page, queryWrapper);
    }

    @Override
    public Page<CommentVO> searchCommentByUser(CommentSearchDTO commentSearchDTO){
        Page<CommentVO> page = new Page<>(commentSearchDTO.getPage(), commentSearchDTO.getLimit());
        page.setOptimizeCountSql(false);

        QueryWrapper<CommentVO> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", commentSearchDTO.getId());
        queryWrapper.eq("c.is_delete", false);
        return commentMapper.selectCommentPage(page, queryWrapper);
    }


    @Override
    public Page<CommentVO> getAuditList(PageSearchDTO pageSearchDTO){
        // 鉴权
        Map<String, Object> map = (Map<String, Object>) ThreadLocalUtil.get();
        Integer type = (Integer) map.get("type");
        if(!type.equals(0)){
            throw new IllegalArgumentException("无管理员权限！");
        }

        Page<CommentVO> page = new Page<>(pageSearchDTO.getPage(), pageSearchDTO.getLimit());
        QueryWrapper<CommentVO> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("audit", false);
        queryWrapper.eq("c.is_delete", false);

        return  commentMapper.selectCommentPage(page, queryWrapper);
    }

}
