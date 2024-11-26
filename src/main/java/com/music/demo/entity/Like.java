package com.music.demo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("`like`")
public class Like {

    /**
     * 点赞的用户id
     */
    private Long userId;

    /**
     * 点赞的歌单id
     */
    private Long playlistId;

    /**
     * 点赞时间
     */
    private LocalDateTime likeTime;

}
