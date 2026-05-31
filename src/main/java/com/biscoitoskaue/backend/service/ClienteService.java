package com.biscoitoskaue.backend.service;

import com.biscoitoskaue.backend.dto.cliente.ClienteResponse;
import com.biscoitoskaue.backend.entity.Cliente;
import com.biscoitoskaue.backend.repository.ClienteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ClienteService {

    private final ClienteRepository clienteRepository;

    public List<ClienteResponse> listarTodos() {
        return clienteRepository.findByAtivoTrue()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public ClienteResponse buscarPorId(Long id) {
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cliente não encontrado"));

        return toResponse(cliente);
    }

    private ClienteResponse toResponse(Cliente cliente) {
        return new ClienteResponse(
                cliente.getId(),
                cliente.getNome(),
                cliente.getCidade(),
                cliente.getTelefone(),
                cliente.getEmail(),
                cliente.getDocumento(),
                cliente.getAtivo()
        );
    }
}