package com.kevin.minoxidilback.repository;

import com.kevin.minoxidilback.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {
    Optional<Usuario> findByEMAIL(String email);
    boolean existsByEMAIL(String email);
}
