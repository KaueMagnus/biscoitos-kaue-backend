package com.biscoitoskaue.backend.repository;

import com.biscoitoskaue.backend.entity.TabelaVenda;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TabelaVendaRepository extends JpaRepository<TabelaVenda, Long> {
    List<TabelaVenda> findAllByOrderByNomeAsc();

    List<TabelaVenda> findByAtivoTrueAndRepresentantesIdOrderByNomeAsc(Long representanteId);

    Optional<TabelaVenda> findByIdAndAtivoTrue(Long id);

    Optional<TabelaVenda> findByIdAndAtivoTrueAndRepresentantesId(Long id, Long representanteId);
}
