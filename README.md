# BootCamp_Projetc
Itaú - Java com Inteligência Artificial

# 🏦 Banco Bootcamp

Aplicativo de banco simples com **backend em Java Spring Boot** (com Spring Security) e **frontend em HTML5 + CSS3 + JS**, usando exclusivamente as cores do Itaú Unibanco.

## ✨ Funcionalidades

- 🔐 **Login e senha** com Spring Security (senha criptografada com BCrypt)
- 🏠 **Tela de início** com dados da conta
- 💰 **Consulta de saldo** em tempo real
- 💸 **Realizar PIX** com validação de saldo insuficiente
- 📄 **Extrato** de transações (depósitos, PIX enviados e recebidos)
- ⚠️ **Contestar transações** pelo extrato (status muda para "Em análise")

## 🛠️ Stack

| Camada | Tecnologia |
|---|---|
| Backend | Java 17+, Spring Boot 3.5, Spring Web, Spring Data JPA |
| Segurança | Spring Security (form login + sessão) |
| Banco | H2 em memória (console em `/h2-console`) |
| Frontend | HTML5, CSS3, JavaScript (fetch API) |

## 🚀 Como executar

```bash
# Compilar
mvn package -DskipTests

# Rodar
java -jar target/banco-app-1.0.0.jar
```

Ou em modo dev:

```bash
mvn spring-boot:run
```

Acesse: **http://localhost:8080**

## 👤 Usuário de teste

| Campo | Valor |
|---|---|
| Usuário | `cliente` |
| Senha | `123456` |

## 🎭 Dados mockados

Toda conta criada pela tela **"Criar conta grátis"** (`/cadastro.html`) já nasce com:

- 💰 **R$ 5.000,00** de saldo
- 📄 **15 transações** de exemplo no extrato (PIX enviados/recebidos e depósitos dos últimos 30 dias)

A lógica está em [DadosMockService.java](src/main/java/com/bootcamp/banco/service/DadosMockService.java).

## 📡 Endpoints da API

| Método | Rota | Descrição |
|---|---|---|
| GET | `/api/conta/saldo` | Saldo e dados da conta |
| POST | `/api/pix` | Realiza um PIX `{chaveDestino, valor, descricao}` |
| GET | `/api/transacoes` | Extrato de transações |
| POST | `/api/transacoes/{id}/contestar` | Contesta uma transação `{motivo}` |

## 📁 Estrutura

```
src/main/java/com/bootcamp/banco/
├── config/        # SecurityConfig, UserDetailsService, DataLoader (seed)
├── controller/    # ContaController, TransacaoController (REST)
├── dto/           # Records de request/response
├── model/         # Usuario, Conta, Transacao + enums
├── repository/    # Repositórios JPA
└── service/       # ContaService (regras de negócio)

src/main/resources/static/
├── login.html / index.html / pix.html / extrato.html
├── css/style.css  # Paleta Itaú (#EC7000, #003767, #106EB0)
└── js/app.js
```

> ⚠️ Projeto didático: CSRF desabilitado e banco em memória. Para produção, habilite CSRF, use um banco real (PostgreSQL/MySQL) e HTTPS.

