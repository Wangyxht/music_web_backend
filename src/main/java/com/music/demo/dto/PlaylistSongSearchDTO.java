package com.music.demo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class PlaylistSongSearchDTO extends PageSearchDTO{

    @Schema(description = "歌单ID", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "歌单ID不能为空")
    Long playlistId;
}
