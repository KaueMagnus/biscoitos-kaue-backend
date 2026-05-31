package com.biscoitoskaue.backend.entity;

import com.biscoitoskaue.backend.enums.PerfilUsuario;
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
@Table(name = "usuarios")
public class Usuario extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String nome;

    @Column(nullable = false, unique = true, length = 150)
    private String email;

    @Column(nullable = false)
    private String senha;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private PerfilUsuario perfil;

    @Builder.Default
    @Column(nullable = false)
    private Boolean ativo = true;

    @OneToMany(mappedBy = "usuario")
    @Builder.Default
    private List<Pedido> pedidos = new ArrayList<>();
}