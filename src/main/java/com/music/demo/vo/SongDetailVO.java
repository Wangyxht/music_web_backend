package com.music.demo.vo;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Locale;

@Data
@EqualsAndHashCode(callSuper=true)
public class SongDetailVO extends SongBaseVO{

    String introduction;

    String lyrics;

    Integer favouriteNum;

    LocalDateTime uploadTime;

    ArrayList<String> tags;

}
