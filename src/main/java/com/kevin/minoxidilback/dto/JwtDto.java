package com.kevin.minoxidilback.dto;

import org.springframework.security.core.authority.SimpleGrantedAuthority;

import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.Set;


public class JwtDto {

    @NotNull
    private String access_token;
    @NotNull
    private String token_type;
    @NotNull
    private Date expires_in;
    @NotNull
    private Set<SimpleGrantedAuthority> auth;

    public JwtDto( ) {}

    public JwtDto(String access_token, String token_type, Date expires_in, Set<SimpleGrantedAuthority> auth) {
        this.access_token = access_token;
        this.token_type = token_type;
        this.expires_in = expires_in;
        this.auth = auth;
    }

    public String getAccess_token() {
        return access_token;
    }

    public void setAccess_token(String access_token) {
        this.access_token = access_token;
    }

    public String getToken_type() {
        return token_type;
    }

    public void setToken_type(String token_type) {
        this.token_type = token_type;
    }

    public Date getExpires_in() {
        return expires_in;
    }

    public void setExpires_in(Date expires_in) {
        this.expires_in = expires_in;
    }

    public Set<SimpleGrantedAuthority> getAuth() {
        return auth;
    }

    public void setAuth(Set<SimpleGrantedAuthority> auth) {
        this.auth = auth;
    }
}
