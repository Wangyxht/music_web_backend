package com.music.demo.dto;


import com.music.demo.enums.CommentTypeEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;



@Data
public class CommentCreateDTO {

    @Schema(description = "歌曲/歌单ID", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "歌曲/歌单ID不能为空")
    private Long entityId;

    @Schema(description = "歌曲/歌单ID", example = "yes", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "评论内容不能为空")
    private String content;


    @Schema(description = "评论类型，0为歌曲，1为歌单", example = "0", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "评类型不能为空")
    private Integer commentType;

}