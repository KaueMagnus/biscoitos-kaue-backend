package com.biscoitoskaue.backend.dto.pedido;

import com.biscoitoskaue.backend.enums.FormaPagamento;
import com.biscoitoskaue.backend.enums.TipoPedido;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record CriarPedidoRequest(
        @NotNull Long clienteId,
        Long tabelaVendaId,
        @NotNull TipoPedido tipo,
        @NotNull FormaPagamento formaPagamento,
        String observacao,
        String motivoTroca,
        @Valid @NotEmpty List<ItemPedidoRequest> itens
) {
}