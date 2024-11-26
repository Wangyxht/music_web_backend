package com.music.demo.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.music.demo.enums.CommentTypeEnum;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("comment")
public class Comment {

    /**
     * 主键id
     */
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 评论的用户id
     */
    private Long userId;

    /**
     * 评论的对象id，歌曲和歌单
     */
    private Long entityId;

    /**
     * 评论时间
     */
    private LocalDateTime commentTime;

    /**
     * 评论内容
     */
    private String content;

    /**
     * 审核状态
     */
    private Boolean audit;

    /**
     * 评论类型
     */
    private CommentTypeEnum commentType;


    @TableLogic(value = "0", delval = "null")
    private Boolean isDelete;
}
