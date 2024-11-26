package com.music.demo.vo;

import com.music.demo.entity.Tags;
import io.swagger.v3.oas.models.security.SecurityScheme;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;
import java.util.ArrayList;

@EqualsAndHashCode(callSuper = true)
@Data
public class PlaylistDetailVO extends PlaylistBaseVO {

    Long userId;

    LocalDateTime createTime;

    String introduction;

    Boolean liked;

    Boolean favourite;

    Integer favouriteCount;

    Integer likeCount;

    ArrayList<String> tags;
}
