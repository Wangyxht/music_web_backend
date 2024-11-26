package com.music.demo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class SmsSendDTO {

    @Schema(description = "手机号", example = "15311485070", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "手机号不能为空")
    @Pattern(regexp = "^1[3-9][0-9]{9}", message = "手机号格式错误。")
    private String phone;

    @Schema(description = "类型", example = "login", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "验证类型不能为空")
    @Pattern(regexp = "register|login|retrieve", message = "验证码类型错误，仅支持 register retrieve 和 login。")
    private String type;
}
