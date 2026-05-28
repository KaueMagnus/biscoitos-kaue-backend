CREATE TABLE usuarios (
                          id BIGSERIAL PRIMARY KEY,
                          nome VARCHAR(150) NOT NULL,
                          email VARCHAR(150) NOT NULL UNIQUE,
                          senha VARCHAR(255) NOT NULL,
                          perfil VARCHAR(30) NOT NULL,
                          ativo BOOLEAN NOT NULL DEFAULT TRUE,
                          created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                          updated_at TIMESTAMP
);

CREATE TABLE clientes (
                          id BIGSERIAL PRIMARY KEY,
                          nome VARCHAR(150) NOT NULL,
                          cidade VARCHAR(100),
                          telefone VARCHAR(30),
                          email VARCHAR(150),
                          documento VARCHAR(30),
                          ativo BOOLEAN NOT NULL DEFAULT TRUE,
                          created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                          updated_at TIMESTAMP
);

CREATE TABLE produtos (
                          id BIGSERIAL PRIMARY KEY,
                          codigo VARCHAR(50) NOT NULL UNIQUE,
                          nome VARCHAR(150) NOT NULL,
                          descricao VARCHAR(500),
                          preco NUMERIC(10,2) NOT NULL,
                          ativo BOOLEAN NOT NULL DEFAULT TRUE,
                          created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                          updated_at TIMESTAMP
);

CREATE TABLE pedidos (
                         id BIGSERIAL PRIMARY KEY,
                         cliente_id BIGINT NOT NULL,
                         usuario_id BIGINT NOT NULL,
                         data_criacao TIMESTAMP NOT NULL,
                         tipo VARCHAR(20) NOT NULL,
                         status VARCHAR(20) NOT NULL,
                         observacao VARCHAR(500),
                         motivo_troca VARCHAR(255),
                         valor_total NUMERIC(12,2) NOT NULL,
                         created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                         updated_at TIMESTAMP,
                         CONSTRAINT fk_pedidos_cliente FOREIGN KEY (cliente_id) REFERENCES clientes(id),
                         CONSTRAINT fk_pedidos_usuario FOREIGN KEY (usuario_id) REFERENCES usuarios(id)
);

CREATE TABLE itens_pedido (
                              id BIGSERIAL PRIMARY KEY,
                              pedido_id BIGINT NOT NULL,
                              produto_id BIGINT NOT NULL,
                              quantidade INTEGER NOT NULL,
                              preco_unitario NUMERIC(10,2) NOT NULL,
                              desconto NUMERIC(10,2) NOT NULL DEFAULT 0,
                              subtotal NUMERIC(10,2) NOT NULL,
                              created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                              updated_at TIMESTAMP,
                              CONSTRAINT fk_itens_pedido_pedido FOREIGN KEY (pedido_id) REFERENCES pedidos(id) ON DELETE CASCADE,
                              CONSTRAINT fk_itens_pedido_produto FOREIGN KEY (produto_id) REFERENCES produtos(id)
);