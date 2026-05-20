# Biscoitos Kauê Backend

API backend do sistema de gestão de pedidos da empresa Biscoitos Kauê.

## Visão Geral

Este módulo é responsável por centralizar as regras de negócio, autenticação, persistência dos dados e integração entre os demais módulos do sistema.

A solução faz parte de um projeto dividido em três frentes principais:

- **Mobile**: aplicação para representantes comerciais registrarem pedidos, trocas e consultarem histórico;
- **Backend**: API responsável por autenticação, regras de negócio, persistência e envio de e-mail;
- **Web**: painel administrativo para acompanhamento de pedidos, clientes e produtos.

## Objetivo do módulo

O backend tem como objetivo:

- autenticar usuários;
- disponibilizar endpoints para clientes, produtos e pedidos;
- persistir informações em banco de dados PostgreSQL;
- centralizar regras de negócio do sistema;
- apoiar a sincronização dos pedidos criados no mobile;
- realizar envio de pedidos por e-mail.

## Tecnologias previstas

- Java
- Spring Boot
- Spring Web
- Spring Data JPA
- Spring Security
- JWT
- PostgreSQL
- Flyway
- JavaMailSender
- Lombok

## Estrutura inicial do projeto

```text
src/main/java/com/biscoitoskaue/api
├── config
├── controller
├── dto
├── entity
├── enums
├── exception
├── repository
├── security
├── service
└── util
