package com.biscoitoskaue.backend.dto.tabelavenda;

import com.biscoitoskaue.backend.dto.representante.RepresentanteResponse;

import java.util.List;

public record TabelaVendaResponse(
        Long id,
        String nome,
        Boolean ativo,
        List<RepresentanteResponse> representantes,
        List<TabelaVendaItemResponse> itens
) {
}
