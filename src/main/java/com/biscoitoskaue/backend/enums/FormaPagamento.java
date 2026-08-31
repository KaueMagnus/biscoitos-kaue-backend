package com.biscoitoskaue.backend.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum FormaPagamento {
    BOLETO_A_VISTA("Boleto a vista"),
    BOLETO_28_DIAS("Boleto 28 dias");

    private final String descricao;

    FormaPagamento(String descricao) {
        this.descricao = descricao;
    }

    @JsonValue
    public String getDescricao() {
        return descricao;
    }

    @JsonCreator
    public static FormaPagamento fromDescricao(String valor) {
        for (FormaPagamento forma : values()) {
            if (forma.descricao.equalsIgnoreCase(valor)) {
                return forma;
            }
        }

        throw new IllegalArgumentException("Forma de pagamento inválida: " + valor);
    }
}
