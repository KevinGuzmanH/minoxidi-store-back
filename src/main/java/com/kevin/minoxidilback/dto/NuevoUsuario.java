package com.kevin.minoxidilback.dto;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import java.util.HashSet;
import java.util.Set;


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

    public String getFIRSTNAME() {
        return FIRSTNAME;
    }

    public void setFIRSTNAME(String FIRSTNAME) {
        this.FIRSTNAME = FIRSTNAME;
    }

    public String getLASTNAME() {
        return LASTNAME;
    }

    public void setLASTNAME(String LASTNAME) {
        this.LASTNAME = LASTNAME;
    }

    public String getEMAIL() {
        return EMAIL;
    }

    public void setEMAIL(String EMAIL) {
        this.EMAIL = EMAIL;
    }

    public String getPROVIDER() {
        return PROVIDER;
    }

    public void setPROVIDER(String PROVIDER) {
        this.PROVIDER = PROVIDER;
    }

    public String getPHONE() {
        return PHONE;
    }

    public void setPHONE(String PHONE) {
        this.PHONE = PHONE;
    }

    public String getPASSWORD() {
        return PASSWORD;
    }

    public void setPASSWORD(String PASSWORD) {
        this.PASSWORD = PASSWORD;
    }

    public Set<String> getROLES() {
        return ROLES;
    }

    public void setROLES(Set<String> ROLES) {
        this.ROLES = ROLES;
    }
}
