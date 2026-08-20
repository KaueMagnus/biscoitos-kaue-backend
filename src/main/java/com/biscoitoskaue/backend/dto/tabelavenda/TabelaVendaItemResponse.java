package com.biscoitoskaue.backend.dto.tabelavenda;

import java.math.BigDecimal;

public record TabelaVendaItemResponse(
        Long produtoId,
        String nomeProduto,
        BigDecimal preco
) {
}
