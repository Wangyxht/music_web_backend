package com.music.demo;

import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.music.demo.dto.SongSearchDTO;
import com.music.demo.mapper.SongMapper;
import com.music.demo.mapper.UserMapper;
import com.music.demo.service.SongService;
import com.music.demo.utils.TextAuditUtil;
import com.music.demo.utils.ThreadLocalUtil;
import com.music.demo.vo.SongBaseVO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.HashMap;
import java.util.Map;

@SpringBootTest
class MusicWebBackendApplicationTests {

    @Test
    void contextLoads() {


    }

}
