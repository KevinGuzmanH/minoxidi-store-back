package com.kevin.minoxidilback.dto;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import java.util.HashSet;
import java.util.Set;

@Data
@Builder
public class NuevoUsuario {

    @NotBlank
    private String FIRSTNAME;
    @NotBlank
    private String LASTNAME;
    @Email
    private String EMAIL;
    @NotBlank
    private String PROVIDER;
    private String PHONE;
    @NotBlank
    private String PASSWORD;
    private Set<String> ROLES;

    public NuevoUsuario(String FIRSTNAME, String LASTNAME, String EMAIL, String PROVIDER, String PHONE, String PASSWORD, Set<String> ROLES) {
        this.FIRSTNAME = FIRSTNAME;
        this.LASTNAME = LASTNAME;
        this.EMAIL = EMAIL;
        this.PROVIDER = PROVIDER;
        this.PHONE = PHONE;
        this.PASSWORD = PASSWORD;
        this.ROLES = ROLES;
    }
}
