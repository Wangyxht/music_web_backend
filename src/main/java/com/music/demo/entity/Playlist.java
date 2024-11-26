package com.music.demo.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("playlist")
public class Playlist {

    /**
     * 主键id
     */
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 建歌单的用户id
     */
    private Long userId;

    /**
     * 建歌单时间
     */
    private LocalDateTime createTime;

    /**
     * 歌单名称
     */
    private String title;

    /**
     * 歌单简介
     */
    private String introduction;


    /**
     * 歌单封面相对路径
     */
    @JsonIgnore
    private String coverPath;

    /**
     * 是否已被删除、注销或封禁
     */
    @TableLogic(value = "0", delval = "null")
    private Boolean isDelete;

}
