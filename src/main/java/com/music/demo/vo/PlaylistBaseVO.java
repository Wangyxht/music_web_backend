package com.music.demo.vo;

import lombok.Data;

@Data
public class PlaylistBaseVO {

    Long id;

    String title;

    String uploader;

    Integer songCount;
}
