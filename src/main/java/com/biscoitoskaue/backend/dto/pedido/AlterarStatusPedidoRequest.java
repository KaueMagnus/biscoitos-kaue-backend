package com.biscoitoskaue.backend.dto.pedido;

import com.biscoitoskaue.backend.enums.StatusPedido;
import jakarta.validation.constraints.NotNull;

public record AlterarStatusPedidoRequest(
        @NotNull StatusPedido status
) {
}
