package com.biscoitoskaue.backend.dto.cliente;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

public record ClienteRequest(
        @Size(max = 150) String nome,
        @Size(max = 100) String cidade,
        @Size(max = 30) String telefone,
        @Email @Size(max = 150) String email,
        @Size(max = 30) String documento,

        @Size(max = 150) String razaoSocial,
        @Size(max = 150) String nomeFantasia,
        @Size(max = 30) String cnpj,
        @Size(max = 30) String inscricaoEstadual,
        @Size(max = 150) String nomeComprador,
        @Size(max = 150) String rua,
        @Size(max = 100) String bairro,
        @Size(max = 2) String estado,
        @Size(max = 20) String cep
) {
}