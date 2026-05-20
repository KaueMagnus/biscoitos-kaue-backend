# Arquitetura do Backend

## 1. Visão Geral

O backend do projeto Biscoitos Kauê será responsável por centralizar a lógica de negócio do sistema, autenticação dos usuários, persistência dos dados e integração entre os módulos mobile e web.

A solução será estruturada em arquitetura cliente-servidor, com API REST consumida pelos seguintes clientes:

- aplicação mobile para representantes comerciais;
- painel web administrativo para acompanhamento operacional.

## 2. Papel do Backend na Solução

O backend será responsável por:

- autenticar usuários;
- autorizar acesso aos recursos conforme perfil;
- fornecer dados de clientes e produtos;
- receber e validar pedidos;
- persistir pedidos e seus itens;
- centralizar regras de negócio;
- enviar e-mails com os dados dos pedidos;
- servir como ponto central de integração entre mobile, web e banco.

## 3. Arquitetura da Solução

### Módulos da solução

- **Mobile (Flutter)**  
  Cliente da API para criação e consulta de pedidos.

- **Backend (Spring Boot)**  
  Núcleo da solução, com autenticação, regras de negócio e persistência.

- **Web (React)**  
  Cliente da API para acompanhamento administrativo.

- **Banco de Dados (PostgreSQL)**  
  Persistência central de usuários, clientes, produtos e pedidos.

### Fluxo de comunicação

- Mobile → API REST
- Web → API REST
- API REST → PostgreSQL

## 4. Arquitetura Interna do Backend

O backend será organizado em camadas, com separação de responsabilidades.

### Pacotes previstos

- `controller`  
  Responsável por expor os endpoints HTTP.

- `service`  
  Responsável pela lógica de negócio.

- `repository`  
  Responsável pelo acesso aos dados via JPA.

- `entity`  
  Responsável pelo mapeamento das entidades persistidas.

- `dto`  
  Responsável pelos objetos de entrada e saída da API.

- `security`  
  Responsável por autenticação, autorização e configuração JWT.

- `config`  
  Responsável por configurações gerais da aplicação.

- `exception`  
  Responsável pelo tratamento padronizado de erros.

## 5. Estratégia de Autenticação

A autenticação será baseada em login com e-mail e senha.

Fluxo previsto:

1. usuário envia credenciais para o endpoint de autenticação;
2. backend valida as credenciais;
3. backend retorna token JWT;
4. clientes da API enviam o token nas requisições protegidas.

Perfis previstos:

- `ADMIN`
- `REPRESENTANTE`

## 6. Estratégia de Persistência

O banco principal será PostgreSQL.

As tabelas principais previstas são:

- usuarios
- clientes
- produtos
- pedidos
- itens_pedido

A evolução do schema será controlada com Flyway.

## 7. Estratégia de Envio por E-mail

O envio de e-mail será tratado no backend.

Fluxo previsto:

1. pedido é recebido pela API;
2. backend valida e persiste o pedido;
3. backend monta o conteúdo do e-mail;
4. backend envia o e-mail para o endereço configurado.

Essa abordagem evita expor credenciais no aplicativo mobile e mantém a lógica centralizada.

## 8. Suporte ao Funcionamento Offline

O funcionamento offline será tratado no módulo mobile com persistência local em SQLite.

O papel do backend nesse fluxo será:

- receber pedidos sincronizados posteriormente;
- validar os dados enviados;
- persistir as informações no banco;
- responder ao app com confirmação da sincronização.

## 9. Ambientes

### Desenvolvimento
Ambiente local para implementação e testes iniciais.

### Homologação
Ambiente publicado para testes integrados entre mobile, backend e web.

### Produção
Ambiente final, caso seja viável na etapa posterior do projeto.

## 10. Estratégia de Deploy

Sugestão atual:

- **Backend:** Render
- **Banco:** Neon
- **Web:** Vercel

Essas escolhas podem ser ajustadas conforme as necessidades do projeto.

## 11. Escopo Inicial da API

Versão inicial prevista:

- autenticação;
- listagem de clientes;
- listagem de produtos;
- criação de pedido normal;
- criação de pedido de troca;
- listagem de pedidos;
- detalhamento de pedido.

## 12. Observações Finais

A arquitetura foi definida com foco em simplicidade, organização e viabilidade de implementação dentro do contexto do PDS, priorizando separação de responsabilidades, centralização das regras de negócio e possibilidade de evolução futura.