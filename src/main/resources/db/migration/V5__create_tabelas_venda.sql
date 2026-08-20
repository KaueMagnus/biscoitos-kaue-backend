CREATE TABLE tabelas_venda (
                              id BIGSERIAL PRIMARY KEY,
                              nome VARCHAR(150) NOT NULL,
                              ativo BOOLEAN NOT NULL DEFAULT TRUE,
                              created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                              updated_at TIMESTAMP
);

CREATE TABLE tabela_venda_representantes (
                              tabela_venda_id BIGINT NOT NULL,
                              representante_id BIGINT NOT NULL,
                              PRIMARY KEY (tabela_venda_id, representante_id),
                              CONSTRAINT fk_tvr_tabela FOREIGN KEY (tabela_venda_id) REFERENCES tabelas_venda(id) ON DELETE CASCADE,
                              CONSTRAINT fk_tvr_representante FOREIGN KEY (representante_id) REFERENCES usuarios(id)
);

CREATE TABLE tabela_venda_itens (
                              id BIGSERIAL PRIMARY KEY,
                              tabela_venda_id BIGINT NOT NULL,
                              produto_id BIGINT NOT NULL,
                              preco NUMERIC(10,2) NOT NULL,
                              created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                              updated_at TIMESTAMP,
                              CONSTRAINT fk_tvi_tabela FOREIGN KEY (tabela_venda_id) REFERENCES tabelas_venda(id) ON DELETE CASCADE,
                              CONSTRAINT fk_tvi_produto FOREIGN KEY (produto_id) REFERENCES produtos(id),
                              CONSTRAINT uq_tvi_tabela_produto UNIQUE (tabela_venda_id, produto_id)
);

ALTER TABLE pedidos
    ADD COLUMN tabela_venda_id BIGINT;

ALTER TABLE pedidos
    ADD CONSTRAINT fk_pedidos_tabela_venda
        FOREIGN KEY (tabela_venda_id) REFERENCES tabelas_venda(id);

CREATE INDEX idx_pedidos_tabela_venda_id ON pedidos(tabela_venda_id);
