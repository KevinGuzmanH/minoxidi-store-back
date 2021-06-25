package com.example.minoxidilback.controller;

import com.example.minoxidilback.dto.JwtDto;
import com.example.minoxidilback.dto.LoginUsuario;
import com.example.minoxidilback.dto.NuevoUsuario;
import com.example.minoxidilback.entity.Rol;
import com.example.minoxidilback.entity.Usuario;
import com.example.minoxidilback.enums.RolNombre;
import com.example.minoxidilback.jwt.JwtEntryPoint;
import com.example.minoxidilback.jwt.JwtProvider;
import com.example.minoxidilback.service.RolService;
import com.example.minoxidilback.service.UsuarioService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
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
    UsuarioService usuarioService;

    @Autowired
    RolService rolService;

    @Autowired
    JwtProvider jwtProvider;

    @PostMapping("/nuevo")
    public ResponseEntity<?> nuevo(@Valid @RequestBody NuevoUsuario nuevoUsuario, BindingResult bindingResult){
        if(bindingResult.hasErrors())
            return new ResponseEntity("Campos mal puestos o email inválido", HttpStatus.BAD_REQUEST);
        if(usuarioService.existsByNombreUsuario(nuevoUsuario.getNombreUsuario()))
            return new ResponseEntity("Ese nombre ya existe", HttpStatus.BAD_REQUEST);
        if(usuarioService.existsByEmail(nuevoUsuario.getEmail()))
            return new ResponseEntity("Ese email ya existe", HttpStatus.BAD_REQUEST);
        Usuario usuario =
                new Usuario(nuevoUsuario.getNombre(),nuevoUsuario.getApellido(), nuevoUsuario.getNombreUsuario(), nuevoUsuario.getEmail(),nuevoUsuario.getSuscribe(),nuevoUsuario.getProvider(),
                        passwordEncoder.encode(nuevoUsuario.getPassword()));
        Set<Rol> roles = new HashSet<>();
        roles.add(rolService.getByRolNombre(RolNombre.ROLE_USER).get());
        if(nuevoUsuario.getRoles().contains("admin"))
            roles.add(rolService.getByRolNombre(RolNombre.ROLE_ADMIN).get());
        usuario.setRoles(roles);
        usuarioService.save(usuario);
        return new ResponseEntity("Usuario guardado", HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<JwtDto> login(@Valid @RequestBody LoginUsuario loginUsuario, BindingResult bindingResult){
        if (!usuarioService.existsByNombreUsuario(loginUsuario.getNombreUsuario())){
            return new ResponseEntity("Esta cuenta no existe por favor registrese", HttpStatus.BAD_REQUEST);

        }
        if(bindingResult.hasErrors())
            return new ResponseEntity("Campos mal puestos", HttpStatus.BAD_REQUEST);
        Authentication authentication =
                authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginUsuario.getNombreUsuario(), loginUsuario.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtProvider.generateToken(authentication);
        UserDetails userDetails = (UserDetails)authentication.getPrincipal();
        JwtDto jwtDto = new JwtDto(jwt, userDetails.getUsername(),usuarioService.getByNombreUsuario(loginUsuario.getNombreUsuario()).get().getEmail(), userDetails.getAuthorities());
        return new ResponseEntity(jwtDto, HttpStatus.OK);
    }
    @PutMapping("/register")
    public ResponseEntity<String> register(@RequestBody String correo){
        Usuario usuario = usuarioService.getByCorreo(correo).get();
        if (usuario.getSuscribe().equals("YES")){
            return new ResponseEntity("Descuida Ya Estas Suscrito",HttpStatus.BAD_REQUEST);
        }
        usuario.setSuscribe("YES");
        usuarioService.save(usuario);
        return new ResponseEntity("Gracias... Ahora Estas Suscrito",HttpStatus.OK);
    }
}
