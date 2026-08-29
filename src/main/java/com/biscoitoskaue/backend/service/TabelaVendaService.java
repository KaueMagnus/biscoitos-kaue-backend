package com.biscoitoskaue.backend.service;

import com.biscoitoskaue.backend.dto.representante.RepresentanteResponse;
import com.biscoitoskaue.backend.dto.tabelavenda.TabelaVendaItemRequest;
import com.biscoitoskaue.backend.dto.tabelavenda.TabelaVendaItemResponse;
import com.biscoitoskaue.backend.dto.tabelavenda.TabelaVendaRequest;
import com.biscoitoskaue.backend.dto.tabelavenda.TabelaVendaResponse;
import com.biscoitoskaue.backend.entity.Produto;
import com.biscoitoskaue.backend.entity.TabelaVenda;
import com.biscoitoskaue.backend.entity.TabelaVendaItem;
import com.biscoitoskaue.backend.entity.Usuario;
import com.biscoitoskaue.backend.enums.PerfilUsuario;
import com.biscoitoskaue.backend.exception.BusinessException;
import com.biscoitoskaue.backend.exception.ForbiddenException;
import com.biscoitoskaue.backend.exception.ResourceNotFoundException;
import com.biscoitoskaue.backend.repository.ProdutoRepository;
import com.biscoitoskaue.backend.repository.TabelaVendaRepository;
import com.biscoitoskaue.backend.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TabelaVendaService {

    private final TabelaVendaRepository tabelaVendaRepository;
    private final UsuarioRepository usuarioRepository;
    private final ProdutoRepository produtoRepository;

    @Transactional(readOnly = true)
    public List<TabelaVendaResponse> listarTodas(String emailUsuarioLogado) {
        Usuario usuario = buscarUsuarioLogado(emailUsuarioLogado);

        List<TabelaVenda> tabelas = usuario.getPerfil() == PerfilUsuario.ADMIN
                ? tabelaVendaRepository.findAllByOrderByNomeAsc()
                : tabelaVendaRepository.findByAtivoTrueAndRepresentantesIdOrderByNomeAsc(usuario.getId());

        return tabelas
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public TabelaVendaResponse buscarPorId(Long id, String emailUsuarioLogado) {
        Usuario usuario = buscarUsuarioLogado(emailUsuarioLogado);

        TabelaVenda tabelaVenda = usuario.getPerfil() == PerfilUsuario.ADMIN
                ? tabelaVendaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tabela de venda não encontrada"))
                : tabelaVendaRepository.findByIdAndAtivoTrueAndRepresentantesId(id, usuario.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Tabela de venda não encontrada"));

        return toResponse(tabelaVenda);
    }

    @Transactional
    public TabelaVendaResponse cadastrar(TabelaVendaRequest request, String emailUsuarioLogado) {
        validarAdmin(emailUsuarioLogado);

        List<Usuario> representantes = buscarRepresentantes(request.representanteIds());

        TabelaVenda tabelaVenda = TabelaVenda.builder()
                .nome(request.nome())
                .ativo(request.ativo() != null ? request.ativo() : true)
                .representantes(representantes)
                .build();

        tabelaVenda.setItens(criarItens(request.itens(), tabelaVenda));

        return toResponse(tabelaVendaRepository.save(tabelaVenda));
    }

    @Transactional
    public TabelaVendaResponse editar(Long id, TabelaVendaRequest request, String emailUsuarioLogado) {
        validarAdmin(emailUsuarioLogado);

        TabelaVenda tabelaVenda = tabelaVendaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tabela de venda não encontrada"));

        List<Usuario> representantes = buscarRepresentantes(request.representanteIds());

        tabelaVenda.setNome(request.nome());
        tabelaVenda.setAtivo(request.ativo() != null ? request.ativo() : tabelaVenda.getAtivo());
        tabelaVenda.setRepresentantes(representantes);

        // Flush a remoção dos itens antigos antes de inserir os novos: sem isso o Hibernate
        // tenta inserir a nova linha antes de apagar a antiga e colide com uq_tvi_tabela_produto
        // quando um produto continua na tabela após a edição.
        tabelaVenda.getItens().clear();
        tabelaVendaRepository.saveAndFlush(tabelaVenda);

        tabelaVenda.getItens().addAll(criarItens(request.itens(), tabelaVenda));

        return toResponse(tabelaVendaRepository.save(tabelaVenda));
    }

    @Transactional
    public TabelaVendaResponse ativar(Long id, String emailUsuarioLogado) {
        validarAdmin(emailUsuarioLogado);

        TabelaVenda tabelaVenda = tabelaVendaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tabela de venda não encontrada"));

        tabelaVenda.setAtivo(true);

        return toResponse(tabelaVendaRepository.save(tabelaVenda));
    }

    @Transactional
    public TabelaVendaResponse inativar(Long id, String emailUsuarioLogado) {
        validarAdmin(emailUsuarioLogado);

        TabelaVenda tabelaVenda = tabelaVendaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tabela de venda não encontrada"));

        tabelaVenda.setAtivo(false);

        return toResponse(tabelaVendaRepository.save(tabelaVenda));
    }

    private List<Usuario> buscarRepresentantes(List<Long> representanteIds) {
        List<Usuario> representantes = usuarioRepository.findAllById(representanteIds);

        if (representantes.size() != representanteIds.size()) {
            throw new ResourceNotFoundException("Representante não encontrado");
        }

        boolean todosSaoRepresentantes = representantes.stream()
                .allMatch(usuario -> usuario.getPerfil() == PerfilUsuario.REPRESENTANTE);

        if (!todosSaoRepresentantes) {
            throw new BusinessException("Só é possível vincular usuários com perfil representante");
        }

        return representantes;
    }

    private List<TabelaVendaItem> criarItens(List<TabelaVendaItemRequest> itensRequest, TabelaVenda tabelaVenda) {
        if (itensRequest == null || itensRequest.isEmpty()) {
            return List.of();
        }

        long produtosDistintos = itensRequest.stream()
                .map(TabelaVendaItemRequest::produtoId)
                .distinct()
                .count();

        if (produtosDistintos != itensRequest.size()) {
            throw new BusinessException("A tabela de venda não pode repetir o mesmo produto");
        }

        return itensRequest.stream()
                .map(itemRequest -> {
                    Produto produto = produtoRepository.findById(itemRequest.produtoId())
                            .orElseThrow(() -> new ResourceNotFoundException("Produto não encontrado"));

                    return TabelaVendaItem.builder()
                            .tabelaVenda(tabelaVenda)
                            .produto(produto)
                            .preco(itemRequest.preco())
                            .build();
                })
                .toList();
    }

    private Usuario buscarUsuarioLogado(String emailUsuarioLogado) {
        return usuarioRepository.findByEmail(emailUsuarioLogado)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado"));
    }

    private void validarAdmin(String emailUsuarioLogado) {
        Usuario usuario = buscarUsuarioLogado(emailUsuarioLogado);

        if (usuario.getPerfil() != PerfilUsuario.ADMIN) {
            throw new ForbiddenException("Somente administradores podem gerenciar tabelas de venda");
        }
    }

    private TabelaVendaResponse toResponse(TabelaVenda tabelaVenda) {
        List<RepresentanteResponse> representantes = tabelaVenda.getRepresentantes().stream()
                .map(representante -> new RepresentanteResponse(
                        representante.getId(),
                        representante.getNome(),
                        representante.getEmail(),
                        representante.getAtivo()
                ))
                .toList();

        List<TabelaVendaItemResponse> itens = tabelaVenda.getItens().stream()
                .map(item -> new TabelaVendaItemResponse(
                        item.getProduto().getId(),
                        item.getProduto().getNome(),
                        item.getPreco()
                ))
                .toList();

        return new TabelaVendaResponse(
                tabelaVenda.getId(),
                tabelaVenda.getNome(),
                tabelaVenda.getAtivo(),
                representantes,
                itens
        );
    }
}
