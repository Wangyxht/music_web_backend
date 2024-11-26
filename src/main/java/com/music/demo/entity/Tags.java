package com.music.demo.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("tags")
public class Tags {

    Long entityId;

    Long tagId;

    Integer type;

}
