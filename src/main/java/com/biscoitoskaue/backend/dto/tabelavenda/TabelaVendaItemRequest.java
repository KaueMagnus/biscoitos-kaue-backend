package com.biscoitoskaue.backend.dto.tabelavenda;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record TabelaVendaItemRequest(
        @NotNull Long produtoId,
        @NotNull @DecimalMin("0.01") BigDecimal preco
) {
}
