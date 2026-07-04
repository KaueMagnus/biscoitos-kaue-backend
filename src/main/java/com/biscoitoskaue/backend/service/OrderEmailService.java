package com.biscoitoskaue.backend.service;

import com.biscoitoskaue.backend.entity.ItemPedido;
import com.biscoitoskaue.backend.entity.Pedido;
import com.biscoitoskaue.backend.enums.TipoPedido;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.scheduling.annotation.Async;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

@Service
@RequiredArgsConstructor
public class OrderEmailService {

    private static final Logger logger = LoggerFactory.getLogger(OrderEmailService.class);
    private static final DateTimeFormatter DATA_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private static final Locale LOCALE_BRASIL = Locale.forLanguageTag("pt-BR");

    private final JavaMailSender mailSender;

    @Value("${app.mail.from:}")
    private String remetente;

    @Value("${app.pedidos.email-destino:}")
    private String destinatarioPedidos;

    @Async
    public void enviarResumoPedido(Pedido pedido) {
        if (destinatarioPedidos == null || destinatarioPedidos.isBlank()) {
            logger.warn("E-mail de destino de pedidos nao configurado. Pedido {} salvo sem envio de e-mail.", pedido.getId());
            return;
        }

        try {
            MimeMessage mensagem = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mensagem, "UTF-8");

            helper.setTo(destinatarioPedidos);
            helper.setSubject("Novo pedido #" + pedido.getId() + " - " + pedido.getCliente().getNome());
            helper.setText(montarResumoPedidoHtml(pedido), true);

            if (remetente != null && !remetente.isBlank()) {
                helper.setFrom(remetente);
            }

            mailSender.send(mensagem);
        } catch (Exception exception) {
            logger.error("Nao foi possivel enviar e-mail do pedido {}.", pedido.getId(), exception);
        }
    }

    private String montarResumoPedidoHtml(Pedido pedido) {
        StringBuilder html = new StringBuilder();

        html.append("""
                <!DOCTYPE html>
                <html>
                <body style="margin:0;padding:0;background-color:#FFF7ED;font-family:Arial,Helvetica,sans-serif;color:#1F1A17;">
                    <table role="presentation" width="100%" cellpadding="0" cellspacing="0" style="background-color:#FFF7ED;padding:24px 0;">
                        <tr>
                            <td align="center">
                                <table role="presentation" width="640" cellpadding="0" cellspacing="0" style="width:640px;max-width:100%;background-color:#FFF1E6;border:1px solid #F6C431;">
                                    <tr>
                                        <td style="background-color:#D9231F;padding:24px 28px;color:#ffffff;">
                                            <div style="font-size:26px;font-weight:bold;line-height:32px;">Biscoitos Kau&ecirc;</div>
                                            <div style="font-size:14px;line-height:20px;color:#FFF1E6;">Sistema de Pedidos</div>
                                        </td>
                                    </tr>
                                    <tr>
                                        <td style="padding:28px;">
                """);

        html.append("<h1 style=\"margin:0 0 16px 0;font-size:24px;line-height:30px;color:#1F1A17;\">Pedido #")
                .append(pedido.getId())
                .append("</h1>");

        html.append("<table role=\"presentation\" cellpadding=\"0\" cellspacing=\"0\" style=\"width:100%;margin-bottom:20px;\">")
                .append("<tr>")
                .append(montarBadge("Tipo", pedido.getTipo().name(), "#B65412"))
                .append(montarBadge("Status", pedido.getStatus().name(), "#D9231F"))
                .append("</tr>")
                .append("</table>");

        html.append("<table role=\"presentation\" cellpadding=\"0\" cellspacing=\"0\" style=\"width:100%;background-color:#ffffff;border:1px solid #F6C431;margin-bottom:22px;\">")
                .append(montarLinhaDetalhe("Cliente", pedido.getCliente().getNome()))
                .append(montarLinhaDetalhe("Representante", pedido.getUsuario().getNome()));

        if (pedido.getDataCriacao() != null) {
            html.append(montarLinhaDetalhe("Data do pedido", pedido.getDataCriacao().format(DATA_FORMATTER)));
        }

        if (temTexto(pedido.getObservacao())) {
            html.append(montarLinhaDetalhe("Observa&ccedil;&atilde;o", pedido.getObservacao()));
        }

        if (pedido.getTipo() == TipoPedido.TROCA && temTexto(pedido.getMotivoTroca())) {
            html.append(montarLinhaDetalhe("Motivo da troca", pedido.getMotivoTroca()));
        }

        html.append("</table>");

        html.append("""
                            <table cellpadding="0" cellspacing="0" style="width:100%;border-collapse:collapse;background-color:#ffffff;border:1px solid #F6C431;">
                                <thead>
                                    <tr>
                                        <th align="left" style="padding:12px;background-color:#B65412;color:#ffffff;font-size:13px;">Produto</th>
                                        <th align="center" style="padding:12px;background-color:#B65412;color:#ffffff;font-size:13px;">Quantidade</th>
                                        <th align="right" style="padding:12px;background-color:#B65412;color:#ffffff;font-size:13px;">Valor unit&aacute;rio</th>
                                        <th align="right" style="padding:12px;background-color:#B65412;color:#ffffff;font-size:13px;">Desconto</th>
                                        <th align="right" style="padding:12px;background-color:#B65412;color:#ffffff;font-size:13px;">Subtotal</th>
                                    </tr>
                                </thead>
                                <tbody>
                """);

        for (ItemPedido item : pedido.getItens()) {
            html.append("<tr>")
                    .append("<td style=\"padding:12px;border-bottom:1px solid #FFF1E6;font-size:14px;color:#1F1A17;\">")
                    .append(escaparHtml(item.getProduto().getNome()))
                    .append("</td>")
                    .append("<td align=\"center\" style=\"padding:12px;border-bottom:1px solid #FFF1E6;font-size:14px;color:#1F1A17;\">")
                    .append(item.getQuantidade())
                    .append("</td>")
                    .append("<td align=\"right\" style=\"padding:12px;border-bottom:1px solid #FFF1E6;font-size:14px;color:#1F1A17;\">")
                    .append(formatarValor(item.getPrecoUnitario()))
                    .append("</td>")
                    .append("<td align=\"right\" style=\"padding:12px;border-bottom:1px solid #FFF1E6;font-size:14px;color:#1F1A17;\">")
                    .append(formatarDesconto(item.getDesconto()))
                    .append("</td>")
                    .append("<td align=\"right\" style=\"padding:12px;border-bottom:1px solid #FFF1E6;font-size:14px;font-weight:bold;color:#1F1A17;\">")
                    .append(formatarValor(item.getSubtotal()))
                    .append("</td>")
                    .append("</tr>");
        }

        html.append("""
                                </tbody>
                            </table>
                """);

        html.append("<table role=\"presentation\" cellpadding=\"0\" cellspacing=\"0\" style=\"width:100%;margin-top:22px;background-color:#D9231F;\">")
                .append("<tr>")
                .append("<td style=\"padding:18px 20px;color:#ffffff;font-size:14px;\">Total do pedido</td>")
                .append("<td align=\"right\" style=\"padding:18px 20px;color:#ffffff;font-size:24px;font-weight:bold;\">")
                .append(formatarValor(pedido.getValorTotal()))
                .append("</td>")
                .append("</tr>")
                .append("</table>");

        html.append("""
                                        </td>
                                    </tr>
                                    <tr>
                                        <td style="padding:18px 28px;background-color:#FFF7ED;color:#6B7280;font-size:12px;line-height:18px;text-align:center;">
                                            E-mail gerado automaticamente pelo sistema Biscoitos Kau&ecirc;.<br>
                                            N&atilde;o &eacute; necess&aacute;rio responder esta mensagem.
                                        </td>
                                    </tr>
                                </table>
                            </td>
                        </tr>
                    </table>
                </body>
                </html>
                """);

        return html.toString();
    }

    private String montarBadge(String rotulo, String valor, String cor) {
        return "<td style=\"padding-right:10px;\">" +
                "<div style=\"background-color:" + cor + ";color:#ffffff;padding:10px 12px;\">" +
                "<div style=\"font-size:11px;color:#FFF1E6;text-transform:uppercase;\">" + rotulo + "</div>" +
                "<div style=\"font-size:15px;font-weight:bold;\">" + escaparHtml(valor) + "</div>" +
                "</div>" +
                "</td>";
    }

    private String montarLinhaDetalhe(String rotulo, String valor) {
        return "<tr>" +
                "<td style=\"width:160px;padding:12px 14px;border-bottom:1px solid #FFF1E6;color:#6B7280;font-size:13px;\">" + rotulo + "</td>" +
                "<td style=\"padding:12px 14px;border-bottom:1px solid #FFF1E6;color:#1F1A17;font-size:14px;font-weight:bold;\">" + escaparHtml(valor) + "</td>" +
                "</tr>";
    }

    private String formatarValor(BigDecimal valor) {
        if (valor == null) {
            valor = BigDecimal.ZERO;
        }

        return NumberFormat.getCurrencyInstance(LOCALE_BRASIL)
                .format(valor)
                .replace("\u00A0", " ");
    }

    private String formatarDesconto(BigDecimal desconto) {
        if (desconto == null || desconto.compareTo(BigDecimal.ZERO) == 0) {
            return "-";
        }

        return formatarValor(desconto);
    }

    private boolean temTexto(String valor) {
        return valor != null && !valor.isBlank();
    }

    private String escaparHtml(String valor) {
        if (valor == null) {
            return "";
        }

        return valor
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");
    }
}
