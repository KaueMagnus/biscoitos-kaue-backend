package com.biscoitoskaue.backend.service;

import com.biscoitoskaue.backend.dto.pedido.CriarPedidoRequest;
import com.biscoitoskaue.backend.dto.pedido.ItemPedidoRequest;
import com.biscoitoskaue.backend.dto.pedido.PedidoResponse;
import com.biscoitoskaue.backend.entity.*;
import com.biscoitoskaue.backend.enums.PerfilUsuario;
import com.biscoitoskaue.backend.enums.TipoPedido;
import com.biscoitoskaue.backend.exception.ForbiddenException;
import com.biscoitoskaue.backend.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PedidoServiceTest {

    @Mock
    private PedidoRepository pedidoRepository;
    @Mock
    private ClienteRepository clienteRepository;
    @Mock
    private ProdutoRepository produtoRepository;
    @Mock
    private UsuarioRepository usuarioRepository;
    @Mock
    private TabelaVendaRepository tabelaVendaRepository;
    @Mock
    private TabelaVendaItemRepository tabelaVendaItemRepository;
    @Mock
    private OrderEmailService orderEmailService;

    @InjectMocks
    private PedidoService pedidoService;

    private Usuario representante;
    private Cliente cliente;
    private Produto produto;

    @BeforeEach
    void setUp() {
        representante = Usuario.builder()
                .id(10L)
                .nome("Fulano")
                .email("fulano@biscoitoskaue.com")
                .perfil(PerfilUsuario.REPRESENTANTE)
                .ativo(true)
                .build();

        cliente = Cliente.builder()
                .id(1L)
                .nome("Cliente Teste")
                .ativo(true)
                .representante(representante)
                .build();

        produto = Produto.builder()
                .id(1L)
                .codigo("COD1")
                .nome("Biscoito")
                .preco(new BigDecimal("10.00"))
                .ativo(true)
                .build();

        when(clienteRepository.findById(1L)).thenReturn(Optional.of(cliente));
        when(usuarioRepository.findByEmail(representante.getEmail())).thenReturn(Optional.of(representante));
        lenient().when(produtoRepository.findById(1L)).thenReturn(Optional.of(produto));
        lenient().when(pedidoRepository.save(any(Pedido.class))).thenAnswer(invocation -> invocation.getArgument(0));
    }

    @Test
    void quandoPedidoSemTabelaDeVenda_usaPrecoPadraoDoProduto() {
        CriarPedidoRequest request = criarRequest(null);

        PedidoResponse response = pedidoService.criarPedido(request, representante.getEmail());

        assertThat(response.itens().get(0).precoUnitario()).isEqualByComparingTo("10.00");
        assertThat(response.tabelaVendaId()).isNull();
    }

    @Test
    void quandoProdutoTemPrecoEspecificoNaTabela_usaPrecoDaTabela() {
        TabelaVenda tabelaVenda = TabelaVenda.builder()
                .id(5L)
                .nome("Tabela Fulano")
                .ativo(true)
                .representantes(List.of(representante))
                .build();

        TabelaVendaItem item = TabelaVendaItem.builder()
                .tabelaVenda(tabelaVenda)
                .produto(produto)
                .preco(new BigDecimal("8.00"))
                .build();

        when(tabelaVendaRepository.findById(5L)).thenReturn(Optional.of(tabelaVenda));
        when(tabelaVendaItemRepository.findByTabelaVendaIdAndProdutoId(5L, 1L))
                .thenReturn(Optional.of(item));

        PedidoResponse response = pedidoService.criarPedido(criarRequest(5L), representante.getEmail());

        assertThat(response.itens().get(0).precoUnitario()).isEqualByComparingTo("8.00");
        assertThat(response.tabelaVendaId()).isEqualTo(5L);
    }

    @Test
    void quandoProdutoNaoEstaNaTabela_caiNoPrecoPadraoDoProduto() {
        TabelaVenda tabelaVenda = TabelaVenda.builder()
                .id(5L)
                .nome("Tabela Fulano")
                .ativo(true)
                .representantes(List.of(representante))
                .build();

        when(tabelaVendaRepository.findById(5L)).thenReturn(Optional.of(tabelaVenda));
        when(tabelaVendaItemRepository.findByTabelaVendaIdAndProdutoId(5L, 1L))
                .thenReturn(Optional.empty());

        PedidoResponse response = pedidoService.criarPedido(criarRequest(5L), representante.getEmail());

        assertThat(response.itens().get(0).precoUnitario()).isEqualByComparingTo("10.00");
    }

    @Test
    void quandoTabelaNaoPertenceAoRepresentante_lancaForbidden() {
        Usuario outroRepresentante = Usuario.builder()
                .id(99L)
                .nome("Outro")
                .email("outro@biscoitoskaue.com")
                .perfil(PerfilUsuario.REPRESENTANTE)
                .ativo(true)
                .build();

        TabelaVenda tabelaVenda = TabelaVenda.builder()
                .id(5L)
                .nome("Tabela de outro representante")
                .ativo(true)
                .representantes(List.of(outroRepresentante))
                .build();

        when(tabelaVendaRepository.findById(5L)).thenReturn(Optional.of(tabelaVenda));

        assertThatThrownBy(() -> pedidoService.criarPedido(criarRequest(5L), representante.getEmail()))
                .isInstanceOf(ForbiddenException.class);
    }

    private CriarPedidoRequest criarRequest(Long tabelaVendaId) {
        return new CriarPedidoRequest(
                1L,
                tabelaVendaId,
                TipoPedido.NORMAL,
                null,
                null,
                List.of(new ItemPedidoRequest(1L, 2, null))
        );
    }
}
