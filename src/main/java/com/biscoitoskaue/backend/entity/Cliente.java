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
@Table(name = "clientes")
public class Cliente extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Campo antigo usado nas telas atuais
    @Column(nullable = false, length = 150)
    private String nome;

    @Column(length = 100)
    private String cidade;

    @Column(length = 30)
    private String telefone;

    @Column(length = 150)
    private String email;

    // Campo antigo usado como documento/CNPJ
    @Column(length = 30)
    private String documento;

    // Novos campos comerciais
    @Column(length = 150)
    private String razaoSocial;

    @Column(length = 150)
    private String nomeFantasia;

    @Column(length = 30)
    private String cnpj;

    @Column(length = 30)
    private String inscricaoEstadual;

    @Column(length = 150)
    private String nomeComprador;

    @Column(length = 150)
    private String rua;

    @Column(length = 100)
    private String bairro;

    @Column(length = 2)
    private String estado;

    @Column(length = 20)
    private String cep;

    @Builder.Default
    @Column(nullable = false)
    private Boolean ativo = true;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "representante_id")
    private Usuario representante;

    @OneToMany(mappedBy = "cliente")
    @Builder.Default
    private List<Pedido> pedidos = new ArrayList<>();
}