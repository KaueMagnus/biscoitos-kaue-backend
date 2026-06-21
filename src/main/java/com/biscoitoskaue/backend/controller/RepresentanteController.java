package com.biscoitoskaue.backend.controller;

import com.biscoitoskaue.backend.dto.representante.RepresentanteRequest;
import com.biscoitoskaue.backend.dto.representante.RepresentanteResponse;
import com.biscoitoskaue.backend.service.RepresentanteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/representantes")
@RequiredArgsConstructor
public class RepresentanteController {

    private final RepresentanteService representanteService;

    @GetMapping
    public ResponseEntity<List<RepresentanteResponse>> listar(Authentication authentication) {
        String emailUsuarioLogado = authentication.getName();
        return ResponseEntity.ok(representanteService.listar(emailUsuarioLogado));
    }

    @PostMapping
    public ResponseEntity<RepresentanteResponse> cadastrar(
            @Valid @RequestBody RepresentanteRequest request,
            Authentication authentication
    ) {
        String emailUsuarioLogado = authentication.getName();
        return ResponseEntity.ok(representanteService.cadastrar(request, emailUsuarioLogado));
    }

    @PatchMapping("/{id}/inativar")
    public ResponseEntity<RepresentanteResponse> inativar(
            @PathVariable Long id,
            Authentication authentication
    ) {
        String emailUsuarioLogado = authentication.getName();
        return ResponseEntity.ok(representanteService.inativar(id, emailUsuarioLogado));
    }
}
