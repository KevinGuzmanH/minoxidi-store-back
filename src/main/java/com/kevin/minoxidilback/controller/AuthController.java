package com.kevin.minoxidilback.controller;

import com.kevin.minoxidilback.dto.EnviarMail;
import com.kevin.minoxidilback.dto.JwtDto;
import com.kevin.minoxidilback.dto.LoginUsuario;
import com.kevin.minoxidilback.dto.NuevoUsuario;
import com.kevin.minoxidilback.entity.Orden;
import com.kevin.minoxidilback.entity.Rol;
import com.kevin.minoxidilback.entity.Usuario;
import com.kevin.minoxidilback.enums.RolNombre;
import com.kevin.minoxidilback.jwt.JwtProvider;
import com.kevin.minoxidilback.service.OrdenService;
import com.kevin.minoxidilback.service.RolService;
import com.kevin.minoxidilback.service.UsuarioService;
import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@RestController
@RequestMapping("/auth")
@CrossOrigin
public class AuthController {

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    OrdenService ordenService;

    @Autowired
    UsuarioService usuarioService;

    @Autowired
    RolService rolService;

    @Autowired
    JwtProvider jwtProvider;

    @Autowired
    EnviarMail enviarMail;

    @Autowired
    Gson gson;

    @PreAuthorize("permitAll()")
    @PostMapping(path = "/new")
    public ResponseEntity<String> nuevo(@RequestBody NuevoUsuario nuevoUsuario){
        if(usuarioService.existsByEmail(nuevoUsuario.getEMAIL()))
            return new ResponseEntity(gson.toJson("Ese email ya esta vinculado a otra cuenta"), HttpStatus.BAD_REQUEST);
         Logger logger = LoggerFactory.getLogger(JwtProvider.class);
        logger.info(nuevoUsuario.getFirstname());
        logger.info(nuevoUsuario.getEMAIL());
        logger.info(nuevoUsuario.getLastname());
        logger.info(nuevoUsuario.getPassword());
        Usuario usuario =
                new Usuario(nuevoUsuario.getFirstname(),nuevoUsuario.getLastname(), nuevoUsuario.getEMAIL(),
                            nuevoUsuario.getPROVIDER(),nuevoUsuario.getPHONE(), passwordEncoder.encode(nuevoUsuario.getPassword()));
        Set<Rol> roles = new HashSet<>();
        roles.add(rolService.getByRolNombre(RolNombre.ROLE_USER).get());
        usuario.setRoles(roles);
        usuarioService.save(usuario);

        this.sendConfirmationEmail(nuevoUsuario.getEMAIL());

        usuario.setRoles(roles);
        return new ResponseEntity(gson.toJson("Revisa tu correo electronico para activar tu cuenta"), HttpStatus.CREATED);
    }

    @PreAuthorize("permitAll()")
    @PostMapping(path = "/login")
    public ResponseEntity<JwtDto> login( @RequestBody LoginUsuario loginUsuario){
        if (!usuarioService.existsByEmail(loginUsuario.getEMAIL())){
            return new ResponseEntity(gson.toJson("Ese Correo Electronico No Está Asociado a Ninguna Cuenta"), HttpStatus.BAD_REQUEST);
        }
        if (!passwordEncoder.matches(loginUsuario.getPASSWORD(),usuarioService.getByEmail(loginUsuario.getEMAIL()).get().getPassword())){
            return new ResponseEntity(gson.toJson("Contraseña Incorrecta"),HttpStatus.BAD_REQUEST);
        }
        if (!usuarioService.getByEmail(loginUsuario.getEMAIL()).get().isEnabled()){
            this.sendConfirmationEmail(loginUsuario.getEMAIL());
            return new ResponseEntity(gson.toJson("Cuenta No Verificada Revisa Tu Correo Electronico"),HttpStatus.UNAUTHORIZED);
        }
        Authentication authentication =
                authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginUsuario.getEMAIL(), loginUsuario.getPASSWORD()));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        JwtDto DTO = jwtProvider.generateToken(authentication);

        DTO.setAuth((Set<SimpleGrantedAuthority>) usuarioService.getByEmail(loginUsuario.getEMAIL()).get().getAuthorities());
        return new ResponseEntity(gson.toJson(DTO), HttpStatus.OK);
    }

    @PreAuthorize("permitAll()")
    @GetMapping(path = "/sendRecoverPwd")
    public ResponseEntity<String> recuperarPws(@RequestHeader ("email") String email ) {
        if (!usuarioService.existsByEmail(email)){
            return new ResponseEntity<String>(gson.toJson("No hay ninguna cuenta asosiada a este correo"),HttpStatus.BAD_REQUEST);
        }
        String token = jwtProvider.generateTokenRecoverPwd(email);

        String link = "https://minoxidilfront.herokuapp.com/inicio/recuperar/changepwd;token=" + token;
        String sendTo = email;
        enviarMail.send(sendTo, link,false );
        return new ResponseEntity<String>(gson.toJson("Revise Su Correo Electronico"),HttpStatus.OK);
    }

    @PreAuthorize("permitAll()")
    @GetMapping(path = "/changePwd")
    public ResponseEntity<String> changePwd(@RequestHeader("access_token") String token,@RequestHeader("newpwd") String newPwd) {
        if (jwtProvider.validateToken(token)){
            Usuario user = usuarioService.getByEmail(jwtProvider.getEmailFromToken(token)).get();
            user.setPASSWORD(passwordEncoder.encode(newPwd));
            usuarioService.save(user);
            return new ResponseEntity<>(gson.toJson("Contraseña Actualizada, Inicia Sesión"),HttpStatus.OK);
        }
        return new ResponseEntity<>(gson.toJson("Sesión Expirada Reintente Por Favor"),HttpStatus.BAD_REQUEST);
    }

    @PreAuthorize("permitAll()")
    @GetMapping(path = "/sendConfirmationEmail")
    public ResponseEntity<String> sendConfirmationEmail(@RequestHeader ("email") String email ) {
        if (!usuarioService.existsByEmail(email)){
            return new ResponseEntity<>("No hay ninguna cuenta asosiada a este correo",HttpStatus.OK);
        }
        String token = jwtProvider.generateTokenConfirmEmail(email);

        String link = "https://minoxidilfront.herokuapp.com/inicio/confirmEmail/verifyToken;token=" + token;
        String sendTo = email;
        enviarMail.send(sendTo, link,true );
        return new ResponseEntity<>(gson.toJson("Email Enviado Revisa Tu Correo Electronico"),HttpStatus.OK);
    }

    @PreAuthorize("permitAll()")
    @GetMapping("/confirmemail")
    public ResponseEntity<String> confirmEmail(@RequestHeader ("access_token") String token) {
        if (jwtProvider.validateToken(token)){
          Usuario user = usuarioService.getByEmail(jwtProvider.getEmailFromToken(token)).get();
            user.setENABLED(true);
            usuarioService.save(user);
            return new ResponseEntity<>(gson.toJson(true),HttpStatus.OK);
        }
        return new ResponseEntity<String>(gson.toJson(false),HttpStatus.UNAUTHORIZED    );
    }

    @PreAuthorize("authenticated")
    @GetMapping(path = "/validateToken")
    public ResponseEntity<String> validarToken(@RequestHeader ("access_token") String token) {
        if (token.isEmpty()) {
            return new ResponseEntity<>(gson.toJson(false), HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(gson.toJson(jwtProvider.validateToken(token)),HttpStatus.OK);
    }

    @PreAuthorize("authenticated")
    @PostMapping(path = "/registerOrder")
    public ResponseEntity<String> registerOrder(@RequestHeader("access_token") String token, @RequestHeader("amount") int amount){
        Date date = new Date();
        Orden newOrder = new Orden(usuarioService.getByEmail(jwtProvider.getEmailFromToken(token)).get(),date,amount);
        ordenService.registerNewOrder(newOrder);
        return new ResponseEntity<>(gson.toJson(true),HttpStatus.OK);
    }

}

