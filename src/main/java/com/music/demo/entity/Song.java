package com.music.demo.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("song")
public class Song {
    /**
     * 主键id
     */
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 上传用户ID
     */
    private Long uploaderId;

    /**
     * 上传时间
     */
    private LocalDateTime uploadTime;

    /**
     * 歌曲名
     */
    private String title;

    /**
     * 歌手
     */
    private String singer;

    /**
     * 歌曲介绍
     */
    private String introduction;

    /**
     * 歌词
     */
    private String lyrics;

    /**
     * 歌曲封面相对路径
     */
    @JsonIgnore
    private String coverPath;

    /**
     * 歌曲音频相对路径
     */
    @JsonIgnore
    private String audioPath;

    /**
     * 审核状态
     */
    private Boolean audit;

    /**
     * 歌曲时长
     */
    private Long duration;

    /**
     * 是否已被删除、注销或封禁
     */
    @TableLogic(value = "0", delval = "null")
    private Boolean isDelete;
}
