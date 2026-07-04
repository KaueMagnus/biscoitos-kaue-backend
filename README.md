# Biscoitos Kauê Backend

API backend do sistema **Biscoitos Kauê**, desenvolvida como parte do Projeto de Desenvolvimento de Software do curso de Análise e Desenvolvimento de Sistemas.

O backend é responsável por centralizar as regras de negócio, autenticação, persistência dos dados, controle de permissões, criação de pedidos, envio automático de e-mails e integração entre o aplicativo mobile dos representantes e o painel administrativo web.

---

## Sobre o projeto

A empresa Biscoitos Kauê possui representantes comerciais que realizam pedidos de produtos para clientes. O objetivo do sistema é digitalizar e organizar esse processo, permitindo que os representantes façam pedidos pelo aplicativo mobile e que a administração acompanhe tudo pelo painel web.

Este repositório contém a API backend da solução.

A solução completa é composta por:

* **Backend:** API REST em Java com Spring Boot;
* **Mobile:** aplicativo Flutter para representantes comerciais;
* **Web:** painel administrativo em React;
* **Banco de dados:** PostgreSQL;
* **Ambiente online de homologação:** backend publicado no Render, banco PostgreSQL no Neon, painel web na Vercel e envio de e-mails pelo Resend.

---

## Funcionalidades do backend

* Autenticação com JWT;
* Controle de usuários por perfil;
* Perfis de acesso:
  * `ADMIN`;
  * `REPRESENTANTE`;
* Cadastro e listagem de clientes;
* Vinculação de clientes ao representante autenticado;
* Cadastro, edição, listagem e inativação de produtos;
* Criação de pedidos normais;
* Criação de pedidos de troca;
* Validação de motivo em pedidos de troca;
* Cálculo automático de subtotais e total do pedido;
* Listagem de pedidos;
* Consulta de detalhes de pedidos;
* Alteração de status de pedidos;
* Cadastro e inativação de representantes;
* Envio automático de e-mail ao criar pedido;
* Template HTML para e-mail de pedido;
* Envio de e-mail de forma assíncrona;
* Integração com API externa de e-mail no ambiente de homologação;
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
* Resend API
* Lombok
* Maven
* Docker
* Docker Compose
* Render
* Neon PostgreSQL

---

## Arquitetura geral

O backend segue uma organização em camadas, separando responsabilidades entre controllers, services, repositories, DTOs, entidades, segurança, configuração e tratamento de exceções.

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

Essa organização permite centralizar regras de negócio no backend e manter o aplicativo mobile e o painel web como consumidores da API.

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
* clientes cadastrados pelo aplicativo mobile são vinculados ao representante autenticado.

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
* administradores podem cadastrar, editar e inativar produtos;
* produtos inativados deixam de aparecer para criação de novos pedidos.

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
* ao criar um pedido, o sistema envia um e-mail automático com o resumo;
* o envio de e-mail ocorre em segundo plano para não bloquear a resposta da API.

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
* representantes inativados não podem acessar o sistema;
* representantes possuem acesso restrito às funcionalidades do aplicativo mobile.

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

Em pedidos do tipo `TROCA`, o motivo da troca deve ser informado.

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
* valores unitários;
* descontos, quando existirem;
* subtotal dos itens;
* total do pedido.

No ambiente de homologação online, o envio de e-mail é realizado por meio da API externa **Resend**. Essa abordagem evita dependência direta de portas SMTP no servidor de hospedagem.

O envio de e-mail é executado de forma assíncrona. Assim, o pedido é salvo e a API retorna resposta ao aplicativo mesmo que ocorra falha no serviço de e-mail.

---

## Ambiente de homologação online

A API foi disponibilizada em ambiente online de homologação para permitir testes integrados entre mobile, backend, banco de dados, painel web e serviço de e-mail.

Serviços utilizados:

* **Backend:** Render
* **Banco de dados:** Neon PostgreSQL
* **Painel web:** Vercel
* **Envio de e-mail:** Resend
* **Aplicativo mobile:** APK Android configurado para consumir a API online

URL da API online:

```text
https://biscoitos-kaue-backend.onrender.com
```

Painel web online:

```text
https://biscoitos-kaue-web.vercel.app/login
```

Este ambiente tem finalidade acadêmica e de demonstração, não representando uma implantação em produção real da empresa.

---

## Pré-requisitos

Antes de rodar o projeto localmente, é necessário ter instalado:

* Java 21;
* Maven ou Maven Wrapper;
* Docker;
* Docker Compose.

---

## Configuração do banco de dados local

O projeto utiliza PostgreSQL.

O arquivo `docker-compose.yml` sobe um container PostgreSQL usando variáveis de ambiente.

Crie um arquivo `.env` na raiz do projeto com valores de desenvolvimento local, por exemplo:

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

## Configuração da aplicação

O backend roda por padrão em:

```text
http://localhost:8080
```

Configurações principais em ambiente local:

```properties
server.port=8080
spring.datasource.url=jdbc:postgresql://localhost:5432/biscoitos_kaue
spring.jpa.hibernate.ddl-auto=validate
spring.flyway.enabled=true
```

Em ambiente online, as credenciais e configurações sensíveis devem ser definidas por variáveis de ambiente na plataforma de hospedagem.

---

## Variáveis de ambiente para homologação

No ambiente de homologação online, as principais variáveis de ambiente são:

```env
DATABASE_URL=
DB_USERNAME=
DB_PASSWORD=
JWT_SECRET=
EMAIL_PROVIDER=resend
RESEND_API_KEY=
MAIL_FROM=
PEDIDOS_EMAIL_DESTINO=
```

Observações:

* não coloque senhas, tokens ou chaves reais no código-fonte;
* as credenciais devem ser configuradas diretamente no Render;
* a chave do Resend deve ser mantida somente em variável de ambiente;
* o banco PostgreSQL em nuvem deve ser configurado pelo Neon.

---

## Como rodar o backend localmente

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

## Como gerar o build

Para gerar o build da aplicação:

```bash
./mvnw clean package -DskipTests
```

O arquivo `.jar` será gerado na pasta:

```text
target/
```

---

## Usuários de teste

Usuários utilizados durante o desenvolvimento e homologação:

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
6. O aplicativo envia o pedido para a API.
7. O backend valida os dados e salva o pedido no banco.
8. O backend envia um e-mail automático com o resumo do pedido.
9. O administrador acessa o painel web.
10. O administrador acompanha os pedidos.
11. O administrador pode alterar o status do pedido.
12. O novo status pode ser consultado posteriormente pelo aplicativo.

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
* filtrar pedidos;
* consultar detalhes de pedidos;
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

O Flyway permite manter o histórico de alterações estruturais do banco e reproduzir a base em diferentes ambientes.

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

API online de homologação:

```text
https://biscoitos-kaue-backend.onrender.com
```

Painel web online:

```text
https://biscoitos-kaue-web.vercel.app/login
```

APK:

[Baixar APK do aplicativo mobile](https://drive.google.com/file/d/1H6BKzXM1XJhrsThZPU9eXw8oMcPUGkhT/view?usp=sharing)

---

## Autor

Desenvolvido por **Kaue Marques Magnus**.

Projeto desenvolvido para a disciplina de Projeto de Desenvolvimento de Software do curso de Análise e Desenvolvimento de Sistemas.

---

## Licença

Projeto acadêmico desenvolvido para fins educacionais.
