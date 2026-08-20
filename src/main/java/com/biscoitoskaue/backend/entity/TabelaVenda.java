package com.biscoitoskaue.backend.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "tabelas_venda")
public class TabelaVenda extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String nome;

    @Builder.Default
    @Column(nullable = false)
    private Boolean ativo = true;

    @ManyToMany
    @JoinTable(
            name = "tabela_venda_representantes",
            joinColumns = @JoinColumn(name = "tabela_venda_id"),
            inverseJoinColumns = @JoinColumn(name = "representante_id")
    )
    @Builder.Default
    private List<Usuario> representantes = new ArrayList<>();

    @OneToMany(mappedBy = "tabelaVenda", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<TabelaVendaItem> itens = new ArrayList<>();
}
