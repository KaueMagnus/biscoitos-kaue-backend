package com.biscoitoskaue.backend.dto.cliente;

public record ClienteResponse(
        Long id,
        String nome,
        String cidade,
        String telefone,
        String email,
        String documento,
        Long representanteId,
        String representanteNome,
        Boolean ativo
) {
}
