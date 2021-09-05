package com.kevin.minoxidilback.jwt;

import com.kevin.minoxidilback.dto.JwtDto;
import com.kevin.minoxidilback.entity.Usuario;
import io.jsonwebtoken.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Component
public class JwtProvider {
    private final static Logger logger = LoggerFactory.getLogger(JwtProvider.class);

    @Value("${jwt.secret}")
    private String SECRET;

    @Value("${jwt.expiration}")
    private int EXPIRATION;

    public JwtDto generateToken(Authentication authentication){
        Usuario user = (Usuario) authentication.getPrincipal();
         String token = Jwts.builder().setSubject(user.getEMAIL())
                .setIssuedAt(new Date())
                .setExpiration(new Date(new Date().getTime() + EXPIRATION * 4))
                .signWith(SignatureAlgorithm.HS512, SECRET)
                .compact();
        return generateJwtDto(token);
    }

    public JwtDto generateJwtDto(String token) {
        Claims parseToken = Jwts.parser().setSigningKey(SECRET).parseClaimsJws(token).getBody();
        Set<SimpleGrantedAuthority> authorities = new HashSet<>();
          JwtDto jwtDto = new JwtDto(token,"Bearer",parseToken.getExpiration(),authorities);
          return jwtDto;
    }

    public String generateTokenRecoverPwd(String correoUser){
        return Jwts.builder().setSubject(correoUser)
                .setIssuedAt(new Date())
                .setExpiration(new Date(new Date().getTime() + EXPIRATION * 1))
                .signWith(SignatureAlgorithm.HS512, SECRET)
                .compact();
    }

    public String generateTokenConfirmEmail(String correoUser){
        return Jwts.builder()
                .setSubject(correoUser)
                .setIssuedAt(new Date())
                .setExpiration(new Date(new Date().getTime() + EXPIRATION * 3))
                .signWith(SignatureAlgorithm.HS512, SECRET)
                .compact();
    }

    public String getEmailFromToken(String token){
        return Jwts.parser().setSigningKey(SECRET).parseClaimsJws(token).getBody().getSubject();
    }

    public boolean validateToken(String token){
        try {
            Jwts.parser().setSigningKey(SECRET).parseClaimsJws(token);
            return true;
        }catch (MalformedJwtException e){
            logger.error("token mal formado");
        }catch (UnsupportedJwtException e){
            logger.error("token no soportado");
        }catch (ExpiredJwtException e){
            logger.error("token expirado");
        }catch (IllegalArgumentException e){
            logger.error("token vacío");
        }catch (SignatureException e){
            logger.error("error en la firma");
        }
        return false;
    }
}
