package com.music.demo.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.baomidou.mybatisplus.annotation.IEnum;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

/**
 * 用户类型
 */
@Getter
public enum UserTypeEnum implements IEnum<Integer> {
    /**
     * 管理员
     */
    ADMIN(0, "ROLE_ADMIN"),
    /**
     * 普通用户
     */
    ORDINARY(1, "ROLE_USER"),
    /**
     * 歌手
     */
    SINGER(2, "ROLE_SINGER");

    @EnumValue
    private final Integer value;

    private final String desc;

    UserTypeEnum(Integer value, String desc) {
        this.value = value;
        this.desc = desc;
    }

    @JsonValue
    public String getDesc() {
        return desc;
    }
}
