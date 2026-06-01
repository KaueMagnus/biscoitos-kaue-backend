package com.biscoitoskaue.backend.dto.pedido;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record ItemPedidoRequest(
        @NotNull Long produtoId,
        @NotNull @Min(1) Integer quantidade,
        BigDecimal desconto
) {
}