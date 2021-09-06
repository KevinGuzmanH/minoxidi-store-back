package com.kevin.minoxidilback.dto;


import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import java.util.Set;


public class NuevoUsuario {

    @NotBlank
    private String firstname;
    @NotBlank
    private String lastname;
    @Email
    private String EMAIL;
    @NotBlank
    private String PROVIDER;
    private String PHONE;
    @NotBlank
    private String password;
    private Set<String> ROLES;

    public NuevoUsuario() {
    }

    public NuevoUsuario(String firstname, String lastname, String EMAIL, String PROVIDER, String PHONE, String password, Set<String> ROLES) {
        this.firstname = firstname;
        this.lastname = lastname;
        this.EMAIL = EMAIL;
        this.PROVIDER = PROVIDER;
        this.PHONE = PHONE;
        this.password = password;
        this.ROLES = ROLES;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Set<String> getROLES() {
        return ROLES;
    }

    public void setROLES(Set<String> ROLES) {
        this.ROLES = ROLES;
    }
}
