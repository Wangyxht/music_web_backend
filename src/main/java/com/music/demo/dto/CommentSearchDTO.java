package com.music.demo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class CommentSearchDTO extends PageSearchDTO{

    @Schema(description = "歌曲/歌单/评论ID", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "歌曲/歌单/评论ID不能为空")
    private Long id;


}
