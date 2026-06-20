ALTER TABLE clientes
    ADD COLUMN representante_id BIGINT;

UPDATE clientes
SET representante_id = usuarios.id
FROM usuarios
WHERE usuarios.email = 'representante@biscoitoskaue.com'
  AND clientes.representante_id IS NULL;

ALTER TABLE clientes
    ADD CONSTRAINT fk_clientes_representante
        FOREIGN KEY (representante_id) REFERENCES usuarios(id);

CREATE INDEX idx_clientes_representante_id ON clientes(representante_id);
