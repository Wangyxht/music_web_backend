package com.music.demo.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.baomidou.mybatisplus.annotation.IEnum;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

/**
 * 歌曲类型
 */
@Getter
public enum FavouriteTypeEnum implements IEnum<Integer> {
    /**
     * 关联歌曲的收藏
     */
    SONG_FAVOURITE(0, "歌曲"),
    /**
     * 关联歌单的点赞
     */
    PLAYLIST_FAVOURITE(1, "歌单");

    @EnumValue
    private final Integer value;

    private final String desc;

    FavouriteTypeEnum(Integer value, String desc) {
        this.value = value;
        this.desc = desc;
    }

    @JsonValue
    public String getDesc() {
        return desc;
    }
}
