package com.kevin.minoxidilback.service;

import com.kevin.minoxidilback.entity.Usuario;
import com.kevin.minoxidilback.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Optional;

@Service
@Transactional
public class UsuarioService {

    @Autowired
    UsuarioRepository usuarioRepository;

    public Optional<Usuario> getByEmail(String email){
        return usuarioRepository.findByEMAIL(email);
    }

    public boolean existsByEmail(String email){
        return usuarioRepository.existsByEMAIL(email);
    }

    public void save(Usuario usuario){
        usuarioRepository.save(usuario);
    }
}
