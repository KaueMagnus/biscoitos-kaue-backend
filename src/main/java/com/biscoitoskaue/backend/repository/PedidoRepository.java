package com.biscoitoskaue.backend.repository;

import com.biscoitoskaue.backend.entity.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PedidoRepository extends JpaRepository<Pedido, Long> {
    List<Pedido> findByUsuarioId(Long usuarioId);

    Optional<Pedido> findByIdAndUsuarioId(Long id, Long usuarioId);
}
