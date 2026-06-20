package com.biscoitoskaue.backend.repository;

import com.biscoitoskaue.backend.entity.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ClienteRepository extends JpaRepository<Cliente, Long> {
    List<Cliente> findByAtivoTrue();

    List<Cliente> findByAtivoTrueAndRepresentanteId(Long representanteId);

    Optional<Cliente> findByIdAndAtivoTrue(Long id);

    Optional<Cliente> findByIdAndAtivoTrueAndRepresentanteId(Long id, Long representanteId);
}
