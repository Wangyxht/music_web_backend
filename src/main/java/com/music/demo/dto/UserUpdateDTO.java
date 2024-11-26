package com.music.demo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UserUpdateDTO {

    private Long id;

    private String username;

    private Integer age;

    private String introduction;

}
