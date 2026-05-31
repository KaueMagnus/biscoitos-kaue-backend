package com.biscoitoskaue.backend.dto.produto;

import java.math.BigDecimal;

public record ProdutoResponse(
        Long id,
        String codigo,
        String nome,
        String descricao,
        BigDecimal preco,
        Boolean ativo
) {
}