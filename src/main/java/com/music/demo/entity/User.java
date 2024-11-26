package com.music.demo.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.music.demo.enums.UserTypeEnum;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("user")
public class User {

    /**
     * 主键id
     */
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 用户名
     */
    @TableField("username")
    private String username;

    /**
     * 密码
     */
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;

    /**
     * 用户类型
     */
    private UserTypeEnum type;

    /**
     * 是否已被删除、注销或封禁
     */
    @TableLogic(value = "0", delval = "null")
    private Boolean isDelete;

    /**
     * 注册时间
     */
    private LocalDateTime registerTime;

    /**
     * 个性签名
     */
    private String introduction;

    /**
     * 年龄
     */
    private Integer age;

    /**
     * 电话号码
     */
    private String phone;

}
