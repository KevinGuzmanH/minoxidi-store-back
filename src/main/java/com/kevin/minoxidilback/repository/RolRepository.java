package com.kevin.minoxidilback.repository;

import com.kevin.minoxidilback.entity.Rol;
import com.kevin.minoxidilback.enums.RolNombre;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RolRepository extends JpaRepository<Rol, Integer> {
    Optional<Rol> findByRolNombre(RolNombre rolNombre);
}
