package com.biscoitoskaue.backend.dto.cliente;

public record ClienteResponse(
        Long id,
        String nome,
        String cidade,
        String telefone,
        String email,
        String documento,

        String razaoSocial,
        String nomeFantasia,
        String cnpj,
        String inscricaoEstadual,
        String nomeComprador,
        String rua,
        String bairro,
        String estado,
        String cep,

        Long representanteId,
        String representanteNome,
        Boolean ativo
) {
}