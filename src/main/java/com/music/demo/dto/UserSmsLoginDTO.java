package com.music.demo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class UserSmsLoginDTO {
    @Schema(description = "手机号", example = "15311485070", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "手机号不能为空")
    @Pattern(regexp = "^1[3-9][0-9]{9}", message = "手机号格式错误。")
    private String phone;

    @Schema(description = "验证码", example = "1234", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "验证码不能为空")
    @Pattern(regexp = "[0-9]{4}", message = "短信验证码格式错误。")
    private String smsCode;
}
