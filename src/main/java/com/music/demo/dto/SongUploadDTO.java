package com.music.demo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.models.security.SecurityScheme;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;


@Data
public class SongUploadDTO {

    @Schema(description = "封面", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "封面文件不能为空")
    private MultipartFile cover;

    @Schema(description = "音频文件", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "音频文件不能为空")
    private MultipartFile audio;

    @Schema(description = "标题", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "标题不能为空")
    private String title;

    @Schema(description = "歌手", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "歌手不能为空")
    private String singer;

    @Schema(description = "简介")
    private String introduction;

    @Schema(description = "歌词")
    private String lyrics;

    @NotNull(message = "标签不能为空")
    private ArrayList<Long> tags;
}