package com.biscoitoskaue.backend.service;

import com.biscoitoskaue.backend.entity.ItemPedido;
import com.biscoitoskaue.backend.entity.Pedido;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
public class OrderEmailService {

    private static final Logger logger = LoggerFactory.getLogger(OrderEmailService.class);
    private static final DateTimeFormatter DATA_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private final JavaMailSender mailSender;

    @Value("${app.mail.from:}")
    private String remetente;

    @Value("${app.pedidos.email-destino:}")
    private String destinatarioPedidos;

    public void enviarResumoPedido(Pedido pedido) {
        if (destinatarioPedidos == null || destinatarioPedidos.isBlank()) {
            logger.warn("E-mail de destino de pedidos nao configurado. Pedido {} salvo sem envio de e-mail.", pedido.getId());
            return;
        }

        try {
            SimpleMailMessage mensagem = new SimpleMailMessage();
            mensagem.setTo(destinatarioPedidos);
            mensagem.setSubject("Novo pedido #" + pedido.getId() + " - Biscoitos Kaue");
            mensagem.setText(montarResumoPedido(pedido));

            if (remetente != null && !remetente.isBlank()) {
                mensagem.setFrom(remetente);
            }

            mailSender.send(mensagem);
        } catch (Exception exception) {
            logger.error("Nao foi possivel enviar e-mail do pedido {}.", pedido.getId(), exception);
        }
    }

    private String montarResumoPedido(Pedido pedido) {
        StringBuilder resumo = new StringBuilder();

        resumo.append("Resumo do pedido #").append(pedido.getId()).append("\n\n");
        resumo.append("Cliente: ").append(pedido.getCliente().getNome()).append("\n");
        resumo.append("Representante: ").append(pedido.getUsuario().getNome()).append("\n");
        resumo.append("Tipo: ").append(pedido.getTipo()).append("\n");
        resumo.append("Status: ").append(pedido.getStatus()).append("\n");
        resumo.append("Data: ").append(pedido.getDataCriacao().format(DATA_FORMATTER)).append("\n");

        if (pedido.getObservacao() != null && !pedido.getObservacao().isBlank()) {
            resumo.append("Observacao: ").append(pedido.getObservacao()).append("\n");
        }

        if (pedido.getMotivoTroca() != null && !pedido.getMotivoTroca().isBlank()) {
            resumo.append("Motivo da troca: ").append(pedido.getMotivoTroca()).append("\n");
        }

        resumo.append("\nItens:\n");

        for (ItemPedido item : pedido.getItens()) {
            resumo.append("- ")
                    .append(item.getProduto().getNome())
                    .append(" | Quantidade: ")
                    .append(item.getQuantidade())
                    .append(" | Preco unitario: ")
                    .append(formatarValor(item.getPrecoUnitario()))
                    .append(" | Desconto: ")
                    .append(formatarValor(item.getDesconto()))
                    .append(" | Subtotal: ")
                    .append(formatarValor(item.getSubtotal()))
                    .append("\n");
        }

        resumo.append("\nValor total: ").append(formatarValor(pedido.getValorTotal())).append("\n");

        return resumo.toString();
    }

    private String formatarValor(BigDecimal valor) {
        if (valor == null) {
            return "R$ 0.00";
        }

        return "R$ " + valor;
    }
}
