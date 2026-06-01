package com.biscoitoskaue.backend.dto.pedido;

import java.math.BigDecimal;

public record ItemPedidoResponse(
        Long id,
        Long produtoId,
        String nomeProduto,
        Integer quantidade,
        BigDecimal precoUnitario,
        BigDecimal desconto,
        BigDecimal subtotal
) {
}