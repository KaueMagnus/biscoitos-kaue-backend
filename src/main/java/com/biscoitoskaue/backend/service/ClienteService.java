package com.biscoitoskaue.backend.service;

import com.biscoitoskaue.backend.dto.cliente.ClienteRequest;
import com.biscoitoskaue.backend.dto.cliente.ClienteResponse;
import com.biscoitoskaue.backend.entity.Cliente;
import com.biscoitoskaue.backend.entity.Usuario;
import com.biscoitoskaue.backend.enums.PerfilUsuario;
import com.biscoitoskaue.backend.exception.BusinessException;
import com.biscoitoskaue.backend.exception.ForbiddenException;
import com.biscoitoskaue.backend.exception.ResourceNotFoundException;
import com.biscoitoskaue.backend.repository.ClienteRepository;
import com.biscoitoskaue.backend.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ClienteService {

    private final ClienteRepository clienteRepository;
    private final UsuarioRepository usuarioRepository;

    @Transactional(readOnly = true)
    public List<ClienteResponse> listarTodos(String emailUsuarioLogado) {
        Usuario usuario = buscarUsuarioLogado(emailUsuarioLogado);

        List<Cliente> clientes = usuario.getPerfil() == PerfilUsuario.ADMIN
                ? clienteRepository.findByAtivoTrue()
                : clienteRepository.findByAtivoTrueAndRepresentanteId(usuario.getId());

        return clientes
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public ClienteResponse buscarPorId(Long id, String emailUsuarioLogado) {
        Usuario usuario = buscarUsuarioLogado(emailUsuarioLogado);

        Cliente cliente = usuario.getPerfil() == PerfilUsuario.ADMIN
                ? clienteRepository.findByIdAndAtivoTrue(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente não encontrado"))
                : clienteRepository.findByIdAndAtivoTrueAndRepresentanteId(id, usuario.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Cliente não encontrado"));

        return toResponse(cliente);
    }

    @Transactional
    public ClienteResponse cadastrar(ClienteRequest request, String emailUsuarioLogado) {
        Usuario usuario = buscarUsuarioLogado(emailUsuarioLogado);

        if (usuario.getPerfil() != PerfilUsuario.REPRESENTANTE) {
            throw new ForbiddenException("Somente representantes podem cadastrar clientes");
        }

        String nomeFantasia = limparTexto(request.nomeFantasia());
        String razaoSocial = limparTexto(request.razaoSocial());
        String nomeAntigo = limparTexto(request.nome());

        String nomeParaCompatibilidade = primeiroTexto(
                nomeFantasia,
                razaoSocial,
                nomeAntigo
        );

        if (nomeParaCompatibilidade == null) {
            throw new BusinessException("Informe a razão social ou o nome fantasia do cliente");
        }

        String cnpj = limparTexto(request.cnpj());
        String documentoAntigo = limparTexto(request.documento());

        String documentoParaCompatibilidade = primeiroTexto(
                cnpj,
                documentoAntigo
        );

        Cliente cliente = Cliente.builder()
                .nome(nomeParaCompatibilidade)
                .cidade(limparTexto(request.cidade()))
                .telefone(limparTexto(request.telefone()))
                .email(limparTexto(request.email()))
                .documento(documentoParaCompatibilidade)

                .razaoSocial(razaoSocial)
                .nomeFantasia(nomeFantasia)
                .cnpj(cnpj)
                .inscricaoEstadual(limparTexto(request.inscricaoEstadual()))
                .nomeComprador(limparTexto(request.nomeComprador()))
                .rua(limparTexto(request.rua()))
                .bairro(limparTexto(request.bairro()))
                .estado(limparTexto(request.estado()))
                .cep(limparTexto(request.cep()))

                .ativo(true)
                .representante(usuario)
                .build();

        return toResponse(clienteRepository.save(cliente));
    }

    private Usuario buscarUsuarioLogado(String emailUsuarioLogado) {
        return usuarioRepository.findByEmail(emailUsuarioLogado)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado"));
    }

    private ClienteResponse toResponse(Cliente cliente) {
        Usuario representante = cliente.getRepresentante();

        return new ClienteResponse(
                cliente.getId(),
                cliente.getNome(),
                cliente.getCidade(),
                cliente.getTelefone(),
                cliente.getEmail(),
                cliente.getDocumento(),

                cliente.getRazaoSocial(),
                cliente.getNomeFantasia(),
                cliente.getCnpj(),
                cliente.getInscricaoEstadual(),
                cliente.getNomeComprador(),
                cliente.getRua(),
                cliente.getBairro(),
                cliente.getEstado(),
                cliente.getCep(),

                representante != null ? representante.getId() : null,
                representante != null ? representante.getNome() : null,
                cliente.getAtivo()
        );
    }

    private String limparTexto(String valor) {
        if (valor == null || valor.trim().isEmpty()) {
            return null;
        }

        return valor.trim();
    }

    private String primeiroTexto(String... valores) {
        for (String valor : valores) {
            if (valor != null && !valor.trim().isEmpty()) {
                return valor.trim();
            }
        }

        return null;
    }
}