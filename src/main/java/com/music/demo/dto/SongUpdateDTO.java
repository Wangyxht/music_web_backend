package com.music.demo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;

@Data
public class SongUpdateDTO {

    @Schema(description = "封面", requiredMode = Schema.RequiredMode.REQUIRED)
    private MultipartFile cover;

    @Schema(description = "音频文件", requiredMode = Schema.RequiredMode.REQUIRED)
    private MultipartFile audio;

    @Schema(description = "标题", requiredMode = Schema.RequiredMode.REQUIRED)
    private String title;

    @Schema(description = "歌手", requiredMode = Schema.RequiredMode.REQUIRED)
    private String singer;

    @Schema(description = "简介")
    private String introduction;

    @Schema(description = "歌词")
    private String lyrics;

    @NotNull(message = "标签不能为空")
    private ArrayList<Long> tags;

    private Long songId;
}
