package com.music.demo.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.baomidou.mybatisplus.annotation.IEnum;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

@Getter
public enum CommentTypeEnum implements IEnum<Integer> {
    /**
     * 关联歌曲的评论
     */
    SONG_COMMENT(0, "SONG"),
    /**
     * 关联歌单的评论
     */
    PLAYLIST_COMMENT(1, "PLAYLIST");

    @EnumValue
    private final Integer value;

    private final String desc;

    CommentTypeEnum(Integer value, String desc) {
        this.value = value;
        this.desc = desc;
    }

    @JsonValue
    public String getDesc() {
        return desc;
    }
}
