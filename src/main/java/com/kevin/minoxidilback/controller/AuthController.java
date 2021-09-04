package com.kevin.minoxidilback.controller;

import com.kevin.minoxidilback.dto.EnviarMail;
import com.kevin.minoxidilback.dto.JwtDto;
import com.kevin.minoxidilback.dto.LoginUsuario;
import com.kevin.minoxidilback.dto.NuevoUsuario;
import com.kevin.minoxidilback.entity.Orden;
import com.kevin.minoxidilback.entity.Rol;
import com.kevin.minoxidilback.entity.Usuario;
import com.kevin.minoxidilback.enums.RolNombre;
import com.kevin.minoxidilback.jwt.JwtEntryPoint;
import com.kevin.minoxidilback.jwt.JwtProvider;
import com.kevin.minoxidilback.service.OrdenService;
import com.kevin.minoxidilback.service.RolService;
import com.kevin.minoxidilback.service.UsuarioService;
import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.text.SimpleDateFormat;
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
    private final static Logger logger = LoggerFactory.getLogger(JwtEntryPoint.class);

    @PreAuthorize("permitAll()")
    @GetMapping(path = "/cookie")
    public ResponseEntity<String> cokkie(HttpServletResponse response){
        Cookie cookie = new Cookie("prueba","valorando");
        response.addCookie(cookie);
        return new ResponseEntity<>(gson.toJson("asd"),HttpStatus.OK);
    }
    @PreAuthorize("permitAll()")
    @GetMapping(path = "/cookieread")
    public ResponseEntity<String> cokkieread(@CookieValue (value = "prueba",defaultValue = "Atta") String cookie){
        logger.info(cookie);
        return new ResponseEntity<>(gson.toJson("asd"),HttpStatus.OK);
    }

    @PreAuthorize("permitAll()")
    @PostMapping(path = "/new",consumes = MediaType.APPLICATION_JSON_VALUE,produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> nuevo(@Valid @RequestBody NuevoUsuario nuevoUsuario, BindingResult bindingResult){
        if(bindingResult.hasErrors())
            return new ResponseEntity(gson.toJson("Campos mal puestos o email inválido"), HttpStatus.BAD_REQUEST);
        if(usuarioService.existsByEmail(nuevoUsuario.getEMAIL()))
            return new ResponseEntity(gson.toJson("Ese email ya esta vinculado a otra cuenta"), HttpStatus.BAD_REQUEST);
        Usuario usuario =
                new Usuario(nuevoUsuario.getFIRSTNAME(),nuevoUsuario.getLASTNAME(), nuevoUsuario.getEMAIL(),
                            nuevoUsuario.getPROVIDER(),nuevoUsuario.getPHONE(), passwordEncoder.encode(nuevoUsuario.getPASSWORD()));
        Set<Rol> roles = new HashSet<>();
        roles.add(rolService.getByRolNombre(RolNombre.ROLE_USER).get());
        usuario.setRoles(roles);
        usuarioService.save(usuario);

        if (!this.sendConfirmationEmail(nuevoUsuario.getEMAIL())){
            return new ResponseEntity<>( gson.toJson("Email Invalido"),HttpStatus.OK);
        }

        usuario.setRoles(roles);
        return new ResponseEntity(gson.toJson("Revisa tu correo electronico para activar tu cuenta"), HttpStatus.CREATED);
    }

    @PreAuthorize("permitAll()")
    @PostMapping(path = "/login",consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<JwtDto> login(@Valid @RequestBody LoginUsuario loginUsuario, BindingResult bindingResult){
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
        if(bindingResult.hasErrors())
            return new ResponseEntity(gson.toJson("Campos mal puestos"), HttpStatus.BAD_REQUEST);
        Logger logger = LoggerFactory.getLogger(JwtEntryPoint.class);
        logger.info(loginUsuario.toString());
        Authentication authentication =
                authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginUsuario.getEMAIL(), loginUsuario.getPASSWORD()));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        JwtDto DTO = jwtProvider.generateToken(authentication);

        DTO.setAuth((Set<SimpleGrantedAuthority>) usuarioService.getByEmail(loginUsuario.getEMAIL()).get().getAuthorities());
        return new ResponseEntity(gson.toJson(DTO), HttpStatus.OK);
    }

    @PreAuthorize("permitAll()")
    @GetMapping(path = "/sendRecoverPwd",consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> recuperarPws(@RequestHeader ("email") String email ) {
        if (!usuarioService.existsByEmail(email)){
            return new ResponseEntity<String>(gson.toJson("No hay ninguna cuenta asosiada a este correo"),HttpStatus.BAD_REQUEST);
        }
        String token = jwtProvider.generateTokenConfirmEmail(email);

        String link = "https://minoxidil-nm.herokuapp.com/inicio/recuperar/changepwd;token=" + token;
        String sendTo = email;
        enviarMail.send(sendTo, link,false );
        return new ResponseEntity<String>(gson.toJson("Revise Su Correo Electronico"),HttpStatus.OK);
    }

    @PreAuthorize("permitAll()")
    @GetMapping(path = "/changePwd",consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
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
    @GetMapping(path = "/sendConfirmationEmail",consumes = MediaType.APPLICATION_JSON_VALUE,produces = MediaType.APPLICATION_JSON_VALUE)
    public boolean sendConfirmationEmail(@RequestHeader ("email") String email ) {
        if (!usuarioService.existsByEmail(email)){
            return false;
        }
        String token = jwtProvider.generateTokenResetPwd(email);

        String link = "https://minoxidil-nm.herokuapp.com/inicio/confirm/confirmEmail;token=" + token;
        String sendTo = email;
        enviarMail.send(sendTo, link,true );
        return true;
    }

    @PreAuthorize("permitAll()")
    @GetMapping("/confirmemail")
    public ResponseEntity<String> confirmEmail(@RequestParam ("access_token") String token) {
        if (jwtProvider.validateToken(token)){
          Usuario user = usuarioService.getByEmail(jwtProvider.getEmailFromToken(token)).get();
            user.setENABLED(true);
            usuarioService.save(user);
            return new ResponseEntity<>(gson.toJson(true),HttpStatus.OK);
        }
        return new ResponseEntity<String>(gson.toJson(false),HttpStatus.OK);
    }

    @PreAuthorize("authenticated")
    @GetMapping(path = "/validateToken",consumes = MediaType.APPLICATION_JSON_VALUE,produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> validarToken(@RequestHeader ("access_token") String token) {
        if (token.isEmpty()) {
            return new ResponseEntity<>(gson.toJson(false), HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(gson.toJson(jwtProvider.validateToken(token)),HttpStatus.OK);
    }

    @PreAuthorize("authenticated")
    @PostMapping(path = "/registerOrder",consumes = MediaType.APPLICATION_JSON_VALUE,produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> registerOrder(@RequestHeader("access_token") String token, @RequestHeader("amount") int amount){
        Date date = new Date();
        Orden newOrder = new Orden(usuarioService.getByEmail(jwtProvider.getEmailFromToken(token)).get(),date,amount);
        ordenService.registerNewOrder(newOrder);
        return new ResponseEntity<>(gson.toJson(true),HttpStatus.OK);
    }

}

