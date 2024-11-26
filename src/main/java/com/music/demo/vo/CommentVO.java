package com.music.demo.vo;


import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CommentVO {

    Long commentId;

    Long userId;

    String username;

    LocalDateTime commentTime;

    String content;
}
