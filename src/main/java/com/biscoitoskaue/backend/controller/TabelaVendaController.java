package com.biscoitoskaue.backend.controller;

import com.biscoitoskaue.backend.dto.tabelavenda.TabelaVendaRequest;
import com.biscoitoskaue.backend.dto.tabelavenda.TabelaVendaResponse;
import com.biscoitoskaue.backend.service.TabelaVendaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tabelas-venda")
@RequiredArgsConstructor
public class TabelaVendaController {

    private final TabelaVendaService tabelaVendaService;

    @GetMapping
    public ResponseEntity<List<TabelaVendaResponse>> listarTodas(Authentication authentication) {
        String emailUsuarioLogado = authentication.getName();
        return ResponseEntity.ok(tabelaVendaService.listarTodas(emailUsuarioLogado));
    }

    @GetMapping("/{id}")
    public ResponseEntity<TabelaVendaResponse> buscarPorId(@PathVariable Long id, Authentication authentication) {
        String emailUsuarioLogado = authentication.getName();
        return ResponseEntity.ok(tabelaVendaService.buscarPorId(id, emailUsuarioLogado));
    }

    @PostMapping
    public ResponseEntity<TabelaVendaResponse> cadastrar(
            @Valid @RequestBody TabelaVendaRequest request,
            Authentication authentication
    ) {
        String emailUsuarioLogado = authentication.getName();
        return ResponseEntity.ok(tabelaVendaService.cadastrar(request, emailUsuarioLogado));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TabelaVendaResponse> editar(
            @PathVariable Long id,
            @Valid @RequestBody TabelaVendaRequest request,
            Authentication authentication
    ) {
        String emailUsuarioLogado = authentication.getName();
        return ResponseEntity.ok(tabelaVendaService.editar(id, request, emailUsuarioLogado));
    }

    @PatchMapping("/{id}/ativar")
    public ResponseEntity<TabelaVendaResponse> ativar(
            @PathVariable Long id,
            Authentication authentication
    ) {
        String emailUsuarioLogado = authentication.getName();
        return ResponseEntity.ok(tabelaVendaService.ativar(id, emailUsuarioLogado));
    }

    @PatchMapping("/{id}/inativar")
    public ResponseEntity<TabelaVendaResponse> inativar(
            @PathVariable Long id,
            Authentication authentication
    ) {
        String emailUsuarioLogado = authentication.getName();
        return ResponseEntity.ok(tabelaVendaService.inativar(id, emailUsuarioLogado));
    }
}
