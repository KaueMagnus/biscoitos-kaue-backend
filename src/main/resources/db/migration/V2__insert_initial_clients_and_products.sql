INSERT INTO clientes (nome, cidade, telefone, email, documento, ativo, created_at)
VALUES
    ('Mercado Silva', 'Joinville', '(47) 99999-1111', 'mercadosilva@email.com', '12345678901', true, CURRENT_TIMESTAMP),
    ('Super União', 'Torres', '(51) 99999-2222', 'superuniao@email.com', '23456789012', true, CURRENT_TIMESTAMP),
    ('Comercial Andrade', 'Santa Rosa', '(55) 99999-3333', 'comercialandrade@email.com', '34567890123', true, CURRENT_TIMESTAMP);

INSERT INTO produtos (codigo, nome, descricao, preco, ativo, created_at)
VALUES
    ('BK001', 'Rosquinha de Polvilho', 'Pacote tradicional de rosquinha de polvilho', 8.50, true, CURRENT_TIMESTAMP),
    ('BK002', 'Suspiro de Merengue', 'Pacote de suspiro de merengue', 10.00, true, CURRENT_TIMESTAMP),
    ('BK003', 'Bolinha de Queijo', 'Pacote de bolinha de queijo', 12.00, true, CURRENT_TIMESTAMP);