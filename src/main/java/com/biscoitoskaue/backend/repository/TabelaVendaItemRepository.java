package com.biscoitoskaue.backend.repository;

import com.biscoitoskaue.backend.entity.TabelaVendaItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TabelaVendaItemRepository extends JpaRepository<TabelaVendaItem, Long> {
    Optional<TabelaVendaItem> findByTabelaVendaIdAndProdutoId(Long tabelaVendaId, Long produtoId);
}
