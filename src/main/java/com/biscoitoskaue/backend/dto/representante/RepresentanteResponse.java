package com.biscoitoskaue.backend.dto.representante;

public record RepresentanteResponse(
        Long id,
        String nome,
        String email,
        Boolean ativo
) {
}
