package com.kevin.minoxidilback.dto;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;


public class LoginUsuario {

    @NotBlank
    private String EMAIL;
    @NotBlank
    private String PASSWORD;

    public LoginUsuario(String EMAIL, String PASSWORD) {
        this.EMAIL = EMAIL;
        this.PASSWORD = PASSWORD;
    }

    public String getEMAIL() {
        return EMAIL;
    }

    public void setEMAIL(String EMAIL) {
        this.EMAIL = EMAIL;
    }

    public String getPASSWORD() {
        return PASSWORD;
    }

    public void setPASSWORD(String PASSWORD) {
        this.PASSWORD = PASSWORD;
    }
}
