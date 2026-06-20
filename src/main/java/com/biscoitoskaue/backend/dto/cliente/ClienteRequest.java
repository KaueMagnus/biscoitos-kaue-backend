package com.biscoitoskaue.backend.dto.cliente;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ClienteRequest(
        @NotBlank @Size(max = 150) String nome,
        @Size(max = 100) String cidade,
        @Size(max = 30) String telefone,
        @Email @Size(max = 150) String email,
        @Size(max = 30) String documento
) {
}
