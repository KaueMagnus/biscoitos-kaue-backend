package com.biscoitoskaue.backend.dto.pedido;

import com.biscoitoskaue.backend.enums.StatusPedido;
import com.biscoitoskaue.backend.enums.TipoPedido;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record PedidoResponse(
        Long id,
        Long clienteId,
        String nomeCliente,
        Long usuarioId,
        String nomeUsuario,
        LocalDateTime dataCriacao,
        TipoPedido tipo,
        StatusPedido status,
        String observacao,
        String motivoTroca,
        BigDecimal valorTotal,
        List<ItemPedidoResponse> itens
) {
}