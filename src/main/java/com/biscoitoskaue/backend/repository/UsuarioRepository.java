package com.biscoitoskaue.backend.repository;

import com.biscoitoskaue.backend.entity.Usuario;
import com.biscoitoskaue.backend.enums.PerfilUsuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findByEmail(String email);

    List<Usuario> findByPerfil(PerfilUsuario perfil);

    Optional<Usuario> findByIdAndPerfil(Long id, PerfilUsuario perfil);
}
