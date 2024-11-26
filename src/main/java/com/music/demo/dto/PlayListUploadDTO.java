package com.music.demo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;

@EqualsAndHashCode(callSuper = true)
@Data
public class PlayListUploadDTO extends PlayListBaseDTO{

    @Schema(description = "封面", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "封面文件不能为空")
    private MultipartFile cover;

    private ArrayList<Long> tags;

}
