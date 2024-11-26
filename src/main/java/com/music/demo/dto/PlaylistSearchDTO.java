package com.music.demo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class PlaylistSearchDTO extends PageSearchDTO{

    @Schema(description = "关键字", example = "1234", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "关键字不能为空")
    private String keyword;

}
