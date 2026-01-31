# Bookie API 

![Java](https://img.shields.io/badge/java-%23ED8B00.svg?style=for-the-badge&logo=openjdk&logoColor=white)
![Spring](https://img.shields.io/badge/spring-%236DB33F.svg?style=for-the-badge&logo=spring&logoColor=white)
![Docker](https://img.shields.io/badge/docker-%230db7ed.svg?style=for-the-badge&logo=docker&logoColor=white)
![AWS](https://img.shields.io/badge/AWS-%23FF9900.svg?style=for-the-badge&logo=amazon-aws&logoColor=white)
![CI/CD Pipeline](https://github.com/matheusrdpa/bookie/actions/workflows/pl.yml/badge.svg)

Backend desenvolvido com Spring Boot para gerenciar livros, autores e a biblioteca pessoal dos usuários.
A ideia do projeto é simples: manter um catálogo organizado, registrar o status de leitura e sugerir novos livros com base no que a pessoa já leu.

## Tecnologias Utilizadas

| Categoria | Tecnologia | 
| :--- | :--- 
| **Linguagem** | Java 
| **Framework** | Spring Boot 
| **Segurança** | Spring Security, JWT
| **Persistência** | Spring Data JPA, Hibernate 
| **Banco de Dados** | PostgreSQL 
| **Utilidades** | MapStruct, Validation 
| **Testes** | JUnit 5, Mockito

## Infraestrutura & DevOps 

* **Containerização:** Docker & Docker Hub
* **CI/CD:** GitHub Actions (Automated Build, Test & Deploy)
* **Cloud Provider:** AWS (EC2 / Amazon Linux 2023)
* **Database:** AWS RDS (PostgreSQL)

## O que a API faz

* **Autenticação com JWT:** Login e registro, com acesso protegido na maior parte dos endpoints.
* **Controle de acesso por roles:** Algumas rotas são exclusivas para ADMIN.
* **Biblioteca pessoal:** Usuários podem salvar livros e marcar como READ, READING ou NOT READ.
* **Recomendações personalizadas:** Sugestão de livros com base nos autores e gêneros que o usuário já leu usando JPA Specifications. (Também aproveitei o projeto para aprender sobre specifications)
* **Filtros e paginação:** Busca por título, autor, rating e combinações desses filtros.

## Como Rodar o Projeto

### Pré-requisitos

* Java 21+
* Maven 3+
* Um servidor PostgreSQL (ou docker se preferir).

### Configuração

1.  **Clone o repositório:**
    ```
    git clone https://github.com/Matheusrdpa/bookie
    cd bookie
    ```
2.  **Configure o Banco de Dados:**
    Edite o arquivo `src/main/resources/application-dev.properties` com as credenciais do seu PostgreSQL.

    ```
    spring.datasource.url=${SPRING_DATA_URL}
    spring.datasource.username=${SPRING_DATA_USERNAME}
    spring.datasource.password=${SPRING_DATA_PASSWORD}
    ```
3.  **Adicione a Chave Secreta do JWT:**
    Adicionar sua chave secreta (variável de ambiente ou arquivo .properties):
    ```
    my-secret-key=SUA_KEY
    ```

### Execução

Pelo Maven:

```
./mvnw spring-boot:run
```


A API estará disponivel em `http://localhost:8080`.

## Autenticação

A maioria dos endpoints requer autenticação via **JWT**, exceto as rotas de registro e login.

* **Registro**: `POST /v1/auth/register`
* **Login**: `POST /v1/auth/login`

Para acessar endpoints protegidos, envie o token JWT no header:

`Authorization: Bearer SEU_TOKEN`

---

## Endpoints da API

| Recurso | Método | Endpoint | Descrição | Nível de Acesso |
| :--- | :--- | :--- | :--- | :--- |
| **Auth** | `POST` | `/v1/auth/register` | Cria um novo usuário. | `permitAll` |
| | `POST` | `/v1/auth/login` | Autentica e retorna o JWT. | `permitAll` |
| **Livros** | `GET` | `/v1/book` | Lista e filtra livros com paginação. | `authenticated` |
| | `GET` | `/v1/book/recommended` | Retorna recomendações de livros para o usuário logado. | `authenticated` |
| | `POST` | `/v1/book` | Cria um novo livro. | `ADMIN` |
| **Biblioteca** | `POST` | `/v1/userbook` | Adiciona um livro à biblioteca do usuário logado. | `USER / ADMIN` |
| | `GET` | `/v1/userbook/{username}/books` | Visualiza a biblioteca de um usuário. | `isAuthenticated()` |

---

## Testes

O projeto tem testes unitarios e de integração com:

* JUnit
* Mockito
-----

Para executar todos os testes, utilize o comando:

```
./mvnw test
