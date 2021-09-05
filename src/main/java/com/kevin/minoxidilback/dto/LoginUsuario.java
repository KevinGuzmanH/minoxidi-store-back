package com.kevin.minoxidilback.dto;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
@Builder
public class LoginUsuario {

    @NotBlank
    private String EMAIL;
    @NotBlank
    private String PASSWORD;

    public LoginUsuario() {}
}
