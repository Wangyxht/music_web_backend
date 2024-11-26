package com.music.demo.vo;

import lombok.Data;

@Data
public class SongBaseVO {

    Long id;

    String title;

    String singer;

    Boolean favourite;

    Long duration;
}
