package com.music.demo.dto;

import com.music.demo.enums.UserTypeEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class UserRegisterDTO {

    @Schema(description = "用户名", example = "wyx", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "用户名不能为空")
    private String username;

    @Schema(description = "密码", example = "123456", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "密码不能为空")
    private String password;

    @Schema(description = "手机号", example = "15311485070", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "手机号不能为空")
    @Pattern(regexp = "^1[3-9][0-9]{9}", message = "手机号格式错误。")
    private String phone;

    @Schema(description = "年龄", example = "18", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer age;

    @Schema(description = "注册类型", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    private UserTypeEnum type;

    @Schema(description = "验证码", example = "1234", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "验证码不能为空")
    @Pattern(regexp = "[0-9]{4}", message = "短信验证码格式错误。")
    private String smsCode;

}
