package com.kevin.minoxidilback.dto;

import lombok.Builder;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import javax.validation.constraints.NotNull;
import java.util.Collection;
import java.util.Date;
import java.util.Set;

@Data
@Builder
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
}
