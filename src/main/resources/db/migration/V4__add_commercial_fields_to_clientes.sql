ALTER TABLE clientes
ADD COLUMN razao_social VARCHAR(150),
ADD COLUMN nome_fantasia VARCHAR(150),
ADD COLUMN cnpj VARCHAR(30),
ADD COLUMN inscricao_estadual VARCHAR(30),
ADD COLUMN nome_comprador VARCHAR(150),
ADD COLUMN rua VARCHAR(150),
ADD COLUMN bairro VARCHAR(100),
ADD COLUMN estado VARCHAR(2),
ADD COLUMN cep VARCHAR(20);

UPDATE clientes
SET nome_fantasia = nome,
    cnpj = documento
WHERE nome_fantasia IS NULL;