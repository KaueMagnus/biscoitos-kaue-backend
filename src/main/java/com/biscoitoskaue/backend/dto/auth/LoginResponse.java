package com.biscoitoskaue.backend.dto.auth;

public record LoginResponse(
        String token,
        String tipo
) {
}