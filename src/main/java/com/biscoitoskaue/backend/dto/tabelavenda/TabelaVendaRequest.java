package com.biscoitoskaue.backend.dto.tabelavenda;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

public record TabelaVendaRequest(
        @NotBlank @Size(max = 150) String nome,
        @NotEmpty List<@NotNull Long> representanteIds,
        @Valid List<TabelaVendaItemRequest> itens,
        Boolean ativo
) {
}
