package com.biscoitoskaue.backend.service;

import com.biscoitoskaue.backend.dto.produto.ProdutoRequest;
import com.biscoitoskaue.backend.dto.produto.ProdutoResponse;
import com.biscoitoskaue.backend.entity.Produto;
import com.biscoitoskaue.backend.entity.Usuario;
import com.biscoitoskaue.backend.enums.PerfilUsuario;
import com.biscoitoskaue.backend.exception.BusinessException;
import com.biscoitoskaue.backend.exception.ForbiddenException;
import com.biscoitoskaue.backend.exception.ResourceNotFoundException;
import com.biscoitoskaue.backend.repository.ProdutoRepository;
import com.biscoitoskaue.backend.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProdutoService {

    private final ProdutoRepository produtoRepository;
    private final UsuarioRepository usuarioRepository;

    @Transactional(readOnly = true)
    public List<ProdutoResponse> listarTodos(String emailUsuarioLogado) {
        Usuario usuario = buscarUsuarioLogado(emailUsuarioLogado);

        List<Produto> produtos = usuario.getPerfil() == PerfilUsuario.ADMIN
                ? produtoRepository.findAll()
                : produtoRepository.findByAtivoTrue();

        return produtos
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public ProdutoResponse buscarPorId(Long id, String emailUsuarioLogado) {
        Usuario usuario = buscarUsuarioLogado(emailUsuarioLogado);

        Produto produto = usuario.getPerfil() == PerfilUsuario.ADMIN
                ? produtoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Produto não encontrado"))
                : produtoRepository.findByIdAndAtivoTrue(id)
                .orElseThrow(() -> new ResourceNotFoundException("Produto não encontrado"));

        return toResponse(produto);
    }

    @Transactional
    public ProdutoResponse cadastrar(ProdutoRequest request, String emailUsuarioLogado) {
        validarAdmin(emailUsuarioLogado);
        validarCodigoDisponivel(request.codigo(), null);

        Produto produto = Produto.builder()
                .codigo(request.codigo())
                .nome(request.nome())
                .descricao(request.descricao())
                .preco(request.preco())
                .ativo(request.ativo() != null ? request.ativo() : true)
                .build();

        return toResponse(produtoRepository.save(produto));
    }

    @Transactional
    public ProdutoResponse editar(Long id, ProdutoRequest request, String emailUsuarioLogado) {
        validarAdmin(emailUsuarioLogado);

        Produto produto = produtoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Produto não encontrado"));

        validarCodigoDisponivel(request.codigo(), id);

        produto.setCodigo(request.codigo());
        produto.setNome(request.nome());
        produto.setDescricao(request.descricao());
        produto.setPreco(request.preco());
        produto.setAtivo(request.ativo() != null ? request.ativo() : produto.getAtivo());

        return toResponse(produtoRepository.save(produto));
    }

    @Transactional
    public ProdutoResponse inativar(Long id, String emailUsuarioLogado) {
        validarAdmin(emailUsuarioLogado);

        Produto produto = produtoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Produto não encontrado"));

        produto.setAtivo(false);

        return toResponse(produtoRepository.save(produto));
    }

    private Usuario buscarUsuarioLogado(String emailUsuarioLogado) {
        return usuarioRepository.findByEmail(emailUsuarioLogado)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado"));
    }

    private void validarAdmin(String emailUsuarioLogado) {
        Usuario usuario = buscarUsuarioLogado(emailUsuarioLogado);

        if (usuario.getPerfil() != PerfilUsuario.ADMIN) {
            throw new ForbiddenException("Somente administradores podem gerenciar produtos");
        }
    }

    private void validarCodigoDisponivel(String codigo, Long idProdutoAtual) {
        produtoRepository.findByCodigo(codigo)
                .filter(produto -> idProdutoAtual == null || !produto.getId().equals(idProdutoAtual))
                .ifPresent(produto -> {
                    throw new BusinessException("Código de produto já cadastrado");
                });
    }

    private ProdutoResponse toResponse(Produto produto) {
        return new ProdutoResponse(
                produto.getId(),
                produto.getCodigo(),
                produto.getNome(),
                produto.getDescricao(),
                produto.getPreco(),
                produto.getAtivo()
        );
    }
}
