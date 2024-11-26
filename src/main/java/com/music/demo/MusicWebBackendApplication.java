package com.music.demo;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
@MapperScan({"com.music.demo.mapper"})
public class MusicWebBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(MusicWebBackendApplication.class, args);
    }

}
