# Modelagem Inicial do Backend

## 1. Objetivo

Este documento apresenta a modelagem inicial das principais entidades do backend do sistema de gestão de pedidos da empresa Biscoitos Kauê.

O foco da modelagem é estruturar o núcleo da solução para suportar autenticação, cadastro e consulta de dados, registro de pedidos e integração entre os módulos do sistema.

## 2. Entidades Principais

### 2.1 Usuario

Representa os usuários autenticados no sistema, incluindo representantes comerciais e administradores.

#### Campos previstos

- `id`
- `nome`
- `email`
- `senha`
- `perfil`
- `ativo`
- `createdAt`
- `updatedAt`

#### Regras

- o e-mail deve ser único;
- o perfil define o tipo de acesso do usuário;
- a senha será armazenada de forma criptografada.

---

### 2.2 Cliente

Representa os clientes atendidos pela empresa.

#### Campos previstos

- `id`
- `nome`
- `cidade`
- `telefone`
- `email`
- `documento`
- `ativo`
- `createdAt`
- `updatedAt`

#### Regras

- o cliente pode possuir vários pedidos;
- clientes inativos não devem ser utilizados em novos pedidos.

---

### 2.3 Produto

Representa os produtos comercializados pela empresa.

#### Campos previstos

- `id`
- `codigo`
- `nome`
- `descricao`
- `preco`
- `ativo`
- `createdAt`
- `updatedAt`

#### Regras

- o código do produto deve ser único;
- o preço será utilizado para compor o subtotal dos itens do pedido.

---

### 2.4 Pedido

Representa o pedido realizado pelo representante comercial.

#### Campos previstos

- `id`
- `cliente`
- `usuario`
- `dataCriacao`
- `tipo`
- `status`
- `observacao`
- `motivoTroca`
- `valorTotal`
- `createdAt`
- `updatedAt`

#### Regras

- todo pedido deve estar vinculado a um cliente;
- todo pedido deve estar vinculado ao usuário que o registrou;
- o pedido deve ter pelo menos um item;
- o valor total será calculado a partir da soma dos subtotais dos itens;
- pedidos do tipo troca poderão registrar motivo e observações específicas.

---

### 2.5 ItemPedido

Representa cada item pertencente a um pedido.

#### Campos previstos

- `id`
- `pedido`
- `produto`
- `quantidade`
- `precoUnitario`
- `desconto`
- `subtotal`
- `createdAt`
- `updatedAt`

#### Regras

- cada item pertence a um único pedido;
- cada item referencia um produto;
- o subtotal será calculado com base em quantidade, preço unitário e desconto;
- o pedido deve conter ao menos um item.

## 3. Enums Principais

### 3.1 PerfilUsuario

Valores previstos:

- `ADMIN`
- `REPRESENTANTE`

---

### 3.2 TipoPedido

Valores previstos:

- `NORMAL`
- `TROCA`

---

### 3.3 StatusPedido

Valores previstos:

- `PENDENTE`
- `ENVIADO`
- `CANCELADO`

## 4. Relacionamentos

### Usuario → Pedido
- um usuário pode registrar vários pedidos;
- um pedido pertence a um único usuário.

### Cliente → Pedido
- um cliente pode ter vários pedidos;
- um pedido pertence a um único cliente.

### Pedido → ItemPedido
- um pedido possui vários itens;
- um item pertence a um único pedido.

### Produto → ItemPedido
- um produto pode aparecer em vários itens de pedido;
- um item de pedido referencia um único produto.

## 5. Regras de Negócio Iniciais

### Pedido normal
- deve possuir cliente;
- deve possuir ao menos um item;
- deve calcular o valor total automaticamente.

### Pedido de troca
- deve possuir cliente;
- deve possuir ao menos um item;
- pode conter motivo de troca;
- pode conter observações específicas.

### Autenticação
- o usuário realiza login com e-mail e senha;
- o backend retorna token JWT para acesso às rotas protegidas.

## 6. Escopo Inicial da V1

A primeira versão do backend deve atender:

- autenticação de usuário;
- consulta de clientes;
- consulta de produtos;
- criação de pedido normal;
- criação de pedido de troca;
- listagem de pedidos;
- detalhamento de pedido.

## 7. Observações

Esta modelagem representa a estrutura inicial do sistema e poderá ser refinada durante o desenvolvimento, desde que as alterações sejam documentadas e mantenham coerência com os objetivos do projeto.