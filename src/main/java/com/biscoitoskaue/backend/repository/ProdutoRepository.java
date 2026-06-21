package com.biscoitoskaue.backend.repository;

import com.biscoitoskaue.backend.entity.Produto;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProdutoRepository extends JpaRepository<Produto, Long> {
    List<Produto> findByAtivoTrue();

    Optional<Produto> findByIdAndAtivoTrue(Long id);

    Optional<Produto> findByCodigo(String codigo);
}
