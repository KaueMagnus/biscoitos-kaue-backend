package com.biscoitoskaue.backend.service;

import com.biscoitoskaue.backend.dto.pedido.*;
import com.biscoitoskaue.backend.entity.*;
import com.biscoitoskaue.backend.enums.PerfilUsuario;
import com.biscoitoskaue.backend.enums.StatusPedido;
import com.biscoitoskaue.backend.enums.TipoPedido;
import com.biscoitoskaue.backend.exception.BusinessException;
import com.biscoitoskaue.backend.exception.ForbiddenException;
import com.biscoitoskaue.backend.exception.ResourceNotFoundException;
import com.biscoitoskaue.backend.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PedidoService {

    private final PedidoRepository pedidoRepository;
    private final ClienteRepository clienteRepository;
    private final ProdutoRepository produtoRepository;
    private final UsuarioRepository usuarioRepository;
    private final OrderEmailService orderEmailService;

    @Transactional
    public PedidoResponse criarPedido(CriarPedidoRequest request, String emailUsuarioLogado) {
        Cliente cliente = clienteRepository.findById(request.clienteId())
                .orElseThrow(() -> new ResourceNotFoundException("Cliente não encontrado"));

        Usuario usuario = usuarioRepository.findByEmail(emailUsuarioLogado)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado"));

        validarPedido(request);
        validarClienteDoRepresentante(cliente, usuario);

        Pedido pedido = new Pedido();
        pedido.setCliente(cliente);
        pedido.setUsuario(usuario);
        pedido.setDataCriacao(LocalDateTime.now());
        pedido.setTipo(request.tipo());
        pedido.setStatus(StatusPedido.PENDENTE);
        pedido.setObservacao(request.observacao());
        pedido.setMotivoTroca(request.motivoTroca());

        List<ItemPedido> itens = request.itens().stream()
                .map(itemRequest -> criarItemPedido(itemRequest, pedido))
                .toList();

        BigDecimal valorTotal = itens.stream()
                .map(ItemPedido::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        pedido.setValorTotal(valorTotal);
        pedido.setItens(itens);

        Pedido pedidoSalvo = pedidoRepository.save(pedido);
        orderEmailService.enviarResumoPedido(pedidoSalvo);

        return toResponse(pedidoSalvo);
    }

    @Transactional(readOnly = true)
    public List<PedidoResponse> listarPedidosDoUsuario(String emailUsuarioLogado) {
        Usuario usuario = usuarioRepository.findByEmail(emailUsuarioLogado)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado"));

        List<Pedido> pedidos = usuario.getPerfil() == PerfilUsuario.ADMIN
                ? pedidoRepository.findAll()
                : pedidoRepository.findByUsuarioId(usuario.getId());

        return pedidos
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public PedidoResponse buscarPorId(Long id, String emailUsuarioLogado) {
        Usuario usuario = usuarioRepository.findByEmail(emailUsuarioLogado)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado"));

        Pedido pedido = usuario.getPerfil() == PerfilUsuario.ADMIN
                ? pedidoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pedido não encontrado"))
                : pedidoRepository.findByIdAndUsuarioId(id, usuario.getId())
                .orElseThrow(() -> new ForbiddenException("Acesso negado ao pedido"));

        return toResponse(pedido);
    }

    @Transactional
    public PedidoResponse alterarStatus(Long id, AlterarStatusPedidoRequest request, String emailUsuarioLogado) {
        Usuario usuario = usuarioRepository.findByEmail(emailUsuarioLogado)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado"));

        if (usuario.getPerfil() != PerfilUsuario.ADMIN) {
            throw new ForbiddenException("Somente administradores podem alterar status de pedido");
        }

        Pedido pedido = pedidoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pedido não encontrado"));

        pedido.setStatus(request.status());

        return toResponse(pedidoRepository.save(pedido));
    }

    private void validarPedido(CriarPedidoRequest request) {
        if (request.itens() == null || request.itens().isEmpty()) {
            throw new BusinessException("O pedido deve possuir ao menos um item");
        }

        if (request.tipo() == TipoPedido.TROCA &&
                (request.motivoTroca() == null || request.motivoTroca().isBlank())) {
            throw new BusinessException("Pedido de troca exige motivo da troca");
        }
    }

    private void validarClienteDoRepresentante(Cliente cliente, Usuario usuario) {
        if (!Boolean.TRUE.equals(cliente.getAtivo())) {
            throw new ResourceNotFoundException("Cliente não encontrado");
        }

        if (usuario.getPerfil() == PerfilUsuario.REPRESENTANTE &&
                (cliente.getRepresentante() == null ||
                        !cliente.getRepresentante().getId().equals(usuario.getId()))) {
            throw new ForbiddenException("Representante não pode criar pedido para cliente de outro representante");
        }
    }

    private ItemPedido criarItemPedido(ItemPedidoRequest request, Pedido pedido) {
        Produto produto = produtoRepository.findById(request.produtoId())
                .orElseThrow(() -> new ResourceNotFoundException("Produto não encontrado"));

        BigDecimal desconto = request.desconto() != null ? request.desconto() : BigDecimal.ZERO;
        BigDecimal precoUnitario = produto.getPreco();
        BigDecimal subtotal = precoUnitario
                .multiply(BigDecimal.valueOf(request.quantidade()))
                .subtract(desconto);

        ItemPedido item = new ItemPedido();
        item.setPedido(pedido);
        item.setProduto(produto);
        item.setQuantidade(request.quantidade());
        item.setPrecoUnitario(precoUnitario);
        item.setDesconto(desconto);
        item.setSubtotal(subtotal);

        return item;
    }

    private PedidoResponse toResponse(Pedido pedido) {
        List<ItemPedidoResponse> itensResponse = pedido.getItens().stream()
                .map(item -> new ItemPedidoResponse(
                        item.getId(),
                        item.getProduto().getId(),
                        item.getProduto().getNome(),
                        item.getQuantidade(),
                        item.getPrecoUnitario(),
                        item.getDesconto(),
                        item.getSubtotal()
                ))
                .toList();

        return new PedidoResponse(
                pedido.getId(),
                pedido.getCliente().getId(),
                pedido.getCliente().getNome(),
                pedido.getUsuario().getId(),
                pedido.getUsuario().getNome(),
                pedido.getDataCriacao(),
                pedido.getTipo(),
                pedido.getStatus(),
                pedido.getObservacao(),
                pedido.getMotivoTroca(),
                pedido.getValorTotal(),
                itensResponse
        );
    }
}
