package com.music.demo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.music.demo.enums.FavouriteTypeEnum;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("favourite")
public class Favourite {
    /**
     * 收藏的用户id
     */
    private Long userId;

    /**
     * 收藏的对象id，歌曲和歌单
     */
    private Long entityId;

    /**
     * 收藏时间
     */
    private LocalDateTime favouriteTime;

    /**
     * 收藏对应类型
     */
    private FavouriteTypeEnum favouriteType;
}
