package com.biscoitoskaue.backend.controller;

import com.biscoitoskaue.backend.dto.produto.ProdutoRequest;
import com.biscoitoskaue.backend.dto.produto.ProdutoResponse;
import com.biscoitoskaue.backend.service.ProdutoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/produtos")
@RequiredArgsConstructor
public class ProdutoController {

    private final ProdutoService produtoService;

    @GetMapping
    public ResponseEntity<List<ProdutoResponse>> listarTodos(Authentication authentication) {
        String emailUsuarioLogado = authentication.getName();
        return ResponseEntity.ok(produtoService.listarTodos(emailUsuarioLogado));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProdutoResponse> buscarPorId(@PathVariable Long id, Authentication authentication) {
        String emailUsuarioLogado = authentication.getName();
        return ResponseEntity.ok(produtoService.buscarPorId(id, emailUsuarioLogado));
    }

    @PostMapping
    public ResponseEntity<ProdutoResponse> cadastrar(
            @Valid @RequestBody ProdutoRequest request,
            Authentication authentication
    ) {
        String emailUsuarioLogado = authentication.getName();
        return ResponseEntity.ok(produtoService.cadastrar(request, emailUsuarioLogado));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProdutoResponse> editar(
            @PathVariable Long id,
            @Valid @RequestBody ProdutoRequest request,
            Authentication authentication
    ) {
        String emailUsuarioLogado = authentication.getName();
        return ResponseEntity.ok(produtoService.editar(id, request, emailUsuarioLogado));
    }

    @PatchMapping("/{id}/inativar")
    public ResponseEntity<ProdutoResponse> inativar(
            @PathVariable Long id,
            Authentication authentication
    ) {
        String emailUsuarioLogado = authentication.getName();
        return ResponseEntity.ok(produtoService.inativar(id, emailUsuarioLogado));
    }
}
