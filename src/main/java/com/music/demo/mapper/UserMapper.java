package com.music.demo.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.music.demo.entity.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper extends BaseMapper<User> {

}
