package com.music.demo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class PageSearchDTO {

    @Schema(description = "页数", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "未指名查询页数。")
    private Integer page;

    @Schema(description = "页面大小", example = "5", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "未指名查询页数。")
    private Integer limit;
}
