package com.biscoitoskaue.backend.controller;

import com.biscoitoskaue.backend.dto.cliente.ClienteRequest;
import com.biscoitoskaue.backend.dto.cliente.ClienteResponse;
import com.biscoitoskaue.backend.service.ClienteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/clientes")
@RequiredArgsConstructor
public class ClienteController {

    private final ClienteService clienteService;

    @GetMapping
    public ResponseEntity<List<ClienteResponse>> listarTodos(Authentication authentication) {
        String emailUsuarioLogado = authentication.getName();
        return ResponseEntity.ok(clienteService.listarTodos(emailUsuarioLogado));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ClienteResponse> buscarPorId(@PathVariable Long id, Authentication authentication) {
        String emailUsuarioLogado = authentication.getName();
        return ResponseEntity.ok(clienteService.buscarPorId(id, emailUsuarioLogado));
    }

    @PostMapping
    public ResponseEntity<ClienteResponse> cadastrar(
            @Valid @RequestBody ClienteRequest request,
            Authentication authentication
    ) {
        String emailUsuarioLogado = authentication.getName();
        return ResponseEntity.ok(clienteService.cadastrar(request, emailUsuarioLogado));
    }
}
