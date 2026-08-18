package com.biscoitoskaue.backend.dto.representante;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RedefinirSenhaRepresentanteRequest(
        @NotBlank @Size(min = 6, max = 100) String novaSenha
) {
}