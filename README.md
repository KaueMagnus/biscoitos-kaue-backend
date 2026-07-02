# Biscoitos Kauê Backend

API backend do sistema **Biscoitos Kauê**, desenvolvido como parte do Projeto de Desenvolvimento de Software do curso de Análise e Desenvolvimento de Sistemas.

O backend é responsável por centralizar as regras de negócio, autenticação, persistência dos dados, controle de permissões e integração entre o aplicativo mobile dos representantes e o painel administrativo web.

---

## Sobre o projeto

A empresa Biscoitos Kauê possui representantes comerciais que realizam pedidos de produtos para clientes. O objetivo do sistema é digitalizar e organizar esse processo, permitindo que os representantes façam pedidos pelo aplicativo mobile e que a administração acompanhe tudo pelo painel web.

Este repositório contém a API backend da solução.

A solução completa é composta por:

* **Backend:** API REST em Java com Spring Boot;
* **Mobile:** aplicativo Flutter para representantes comerciais;
* **Web:** painel administrativo em React;
* **Banco de dados:** PostgreSQL.

---

## Funcionalidades do backend

* Autenticação com JWT;
* Controle de usuários por perfil;
* Perfis de acesso:

    * `ADMIN`;
    * `REPRESENTANTE`;
* Cadastro e listagem de clientes;
* Vinculação de clientes ao representante;
* Cadastro, edição, listagem e inativação de produtos;
* Criação de pedidos normais;
* Criação de pedidos de troca;
* Listagem de pedidos;
* Consulta de detalhes de pedidos;
* Alteração de status de pedidos;
* Cadastro e inativação de representantes;
* Envio automático de e-mail ao criar pedido;
* Template HTML para e-mail de pedido;
* Tratamento global de erros;
* Tratamento de token expirado/inválido com HTTP 401;
* Migrations com Flyway;
* Persistência com PostgreSQL.

---

## Tecnologias utilizadas

* Java 21
* Spring Boot
* Spring Web
* Spring Data JPA
* Spring Security
* JWT
* PostgreSQL
* Flyway
* Spring Mail
* Lombok
* Maven
* Docker
* Docker Compose

---

## Arquitetura geral

O backend segue uma organização em camadas, separando responsabilidades entre controllers, services, repositories, DTOs, entidades, segurança e tratamento de exceções.

Estrutura principal:

```text
src/main/java/com/biscoitoskaue/backend
├── config
├── controller
├── dto
├── entity
├── enums
├── exception
├── repository
├── security
└── service
```

---

## Principais módulos

### Autenticação

A autenticação é feita por JWT.

Endpoint principal:

```http
POST /auth/login
```

Após o login, o usuário recebe um token JWT que deve ser enviado nas próximas requisições protegidas.

---

### Clientes

Endpoints principais:

```http
GET /clientes
GET /clientes/{id}
POST /clientes
```

Regras principais:

* representantes visualizam apenas seus próprios clientes;
* administradores podem visualizar todos os clientes;
* clientes cadastrados pelo app mobile são vinculados ao representante autenticado.

---

### Produtos

Endpoints principais:

```http
GET /produtos
GET /produtos/{id}
POST /produtos
PUT /produtos/{id}
PATCH /produtos/{id}/inativar
```

Regras principais:

* representantes visualizam produtos disponíveis;
* administradores podem cadastrar, editar e inativar produtos.

---

### Pedidos

Endpoints principais:

```http
POST /pedidos
GET /pedidos
GET /pedidos/{id}
PATCH /pedidos/{id}/status
```

Regras principais:

* representantes criam pedidos pelo aplicativo mobile;
* representantes visualizam seus próprios pedidos;
* administradores visualizam todos os pedidos;
* administradores podem alterar o status dos pedidos;
* pedidos podem ser do tipo `NORMAL` ou `TROCA`;
* pedidos de troca devem conter motivo da troca;
* o backend calcula os totais do pedido;
* ao criar um pedido, o sistema envia um e-mail automático com o resumo.

---

### Representantes

Endpoints principais:

```http
GET /representantes
POST /representantes
PATCH /representantes/{id}/inativar
```

Regras principais:

* apenas administradores podem gerenciar representantes;
* representantes inativados não podem acessar o sistema.

---

## Status de pedido

Os pedidos podem possuir os seguintes status:

```text
PENDENTE
ENVIADO
CANCELADO
```

---

## Tipos de pedido

Os pedidos podem ser:

```text
NORMAL
TROCA
```

---

## E-mail automático de pedido

Ao criar um pedido, o backend envia automaticamente um e-mail com o resumo do pedido para o endereço configurado nas variáveis de ambiente.

O e-mail contém:

* número do pedido;
* tipo do pedido;
* status;
* cliente;
* representante;
* data do pedido;
* observação, se existir;
* motivo da troca, se for pedido de troca;
* tabela de itens;
* total do pedido.

O envio de e-mail não impede a criação do pedido. Caso ocorra erro no envio, o pedido continua salvo no banco.

---

## Pré-requisitos

Antes de rodar o projeto, é necessário ter instalado:

* Java 21;
* Maven ou Maven Wrapper;
* Docker;
* Docker Compose.

---

## Configuração do banco de dados

O projeto utiliza PostgreSQL.

O arquivo `docker-compose.yml` sobe um container PostgreSQL usando variáveis de ambiente.

Crie um arquivo `.env` na raiz do projeto com:

```env
POSTGRES_DB=biscoitos_kaue
POSTGRES_USER=postgres
POSTGRES_PASSWORD=postgres
POSTGRES_PORT=5432
```

Suba o banco:

```bash
docker compose up -d
```

---

## Configuração de e-mail

Para o envio de e-mail funcionar, configure as variáveis de ambiente:

```env
MAIL_HOST=smtp.gmail.com
MAIL_PORT=587
MAIL_USERNAME=seu_email@gmail.com
MAIL_PASSWORD=sua_senha_de_app
MAIL_FROM=seu_email@gmail.com
PEDIDOS_EMAIL_DESTINO=email_destino@exemplo.com
```

Observação:

Não coloque senhas reais no código-fonte ou no repositório. Para Gmail, utilize senha de app.

---

## Configuração da aplicação

O backend roda por padrão em:

```text
http://localhost:8080
```

Configurações principais:

```properties
server.port=8080
spring.datasource.url=jdbc:postgresql://localhost:5432/biscoitos_kaue
spring.jpa.hibernate.ddl-auto=validate
spring.flyway.enabled=true
```

---

## Como rodar o backend

Clone o repositório:

```bash
git clone https://github.com/KaueMagnus/biscoitos-kaue-backend.git
```

Acesse a pasta do projeto:

```bash
cd biscoitos-kaue-backend
```

Suba o banco de dados:

```bash
docker compose up -d
```

Rode a aplicação:

```bash
./mvnw spring-boot:run
```

A API ficará disponível em:

```text
http://localhost:8080
```

---

## Usuários de teste

Usuários utilizados durante o desenvolvimento:

```text
ADMIN
E-mail: admin@biscoitoskaue.com
Senha: 123456
```

```text
REPRESENTANTE
E-mail: representante@biscoitoskaue.com
Senha: 123456
```

Observação: os usuários podem variar conforme os dados iniciais configurados no banco.

---

## Fluxo principal do sistema

1. O representante acessa o aplicativo mobile.
2. O representante faz login.
3. O representante cadastra ou seleciona um cliente.
4. O representante consulta os produtos disponíveis.
5. O representante cria um pedido normal ou de troca.
6. O backend salva o pedido no banco de dados.
7. O backend envia um e-mail automático com o resumo do pedido.
8. O administrador acessa o painel web.
9. O administrador acompanha os pedidos.
10. O administrador pode alterar o status do pedido.

---

## Integração com os outros módulos

### Aplicativo mobile

O aplicativo mobile consome a API para:

* autenticar representantes;
* listar clientes;
* cadastrar clientes;
* listar produtos;
* criar pedidos;
* listar pedidos;
* visualizar detalhes do pedido.

### Painel web

O painel administrativo consome a API para:

* autenticar administradores;
* visualizar dashboard;
* listar pedidos;
* alterar status de pedidos;
* gerenciar produtos;
* gerenciar representantes.

---

## Tratamento de erros

A API possui tratamento global de erros para retornar respostas padronizadas.

Exemplos de status tratados:

```text
400 Bad Request
401 Unauthorized
403 Forbidden
404 Not Found
500 Internal Server Error
```

Tokens expirados ou inválidos retornam HTTP 401.

---

## Migrations

O projeto utiliza Flyway para controle de versão do banco de dados.

As migrations ficam em:

```text
src/main/resources/db/migration
```

---

## Segurança

A API utiliza Spring Security com autenticação baseada em JWT.

Regras principais:

* rotas públicas: login;
* rotas protegidas: clientes, produtos, pedidos e representantes;
* permissões controladas de acordo com o perfil do usuário;
* representantes acessam apenas dados permitidos;
* administradores possuem acesso às funções administrativas.

---

## Links importantes

Backend:

```text
https://github.com/KaueMagnus/biscoitos-kaue-backend
```

Mobile:

```text
https://github.com/KaueMagnus/biscoitos-kaue-mobile
```

Web:

```text
https://github.com/KaueMagnus/biscoitos-kaue-web
```

APK:

[Baixar APK do aplicativo mobile](https://drive.google.com/file/d/1AYGlK3rkz0xZrCHLfP76-dT7p7OwhFVs/view?usp=sharing)

---

## Autor

Desenvolvido por **Kaue Marques Magnus**.

Projeto desenvolvido para a disciplina de Projeto de Desenvolvimento de Software do curso de Análise e Desenvolvimento de Sistemas.

---

## Licença

Projeto acadêmico desenvolvido para fins educacionais.
