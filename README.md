# 🚗 Sistema de Aluguel de Carros 👨‍💻

> [!NOTE]
> Sistema web para **efetuar, modificar, consultar, cancelar e avaliar pedidos de aluguel de automóveis**, com login obrigatório para clientes, empresas e bancos.

<table>
  <tr>
    <td width="800px">
      <div align="justify">
        Projeto do <b>Laboratório 02 — DIAW</b> (Desenvolvimento e Integração de Aplicações Web), curso de Engenharia de Software, sob orientação do <a href="https://github.com/joaopauloaramuni">Prof. Dr. João Paulo Aramuni</a>. O sistema permite que <b>clientes</b> introduzam pedidos de aluguel e que <b>agentes</b> (empresas e bancos) os analisem financeiramente e executem o contrato, com opção de <b>contrato de crédito</b> concedido por um banco. Foi desenvolvido com <b>Spring Boot</b> (arquitetura MVC) no back-end, <b>React + Vite</b> no front-end e <b>Supabase (PostgreSQL)</b> como banco de dados.
      </div>
    </td>
    <td>
      <div>
        <img src="https://joaopauloaramuni.github.io/image/logo_ES_vertical.png" alt="Logo do Projeto" width="120px"/>
      </div>
    </td>
  </tr>
</table>

---

## 🚧 Status do Projeto

![Versão](https://img.shields.io/badge/Versão-v1.0.0-blue?style=for-the-badge) ![Java](https://img.shields.io/badge/Java-25-007ec6?style=for-the-badge&logo=openjdk&logoColor=white) ![Maven](https://img.shields.io/badge/Maven-3.9-007ec6?style=for-the-badge&logo=apachemaven&logoColor=white) ![Spring Boot](https://img.shields.io/badge/Spring_Boot-4.0.x-007ec6?style=for-the-badge&logo=springboot&logoColor=white) ![React](https://img.shields.io/badge/React-19-007ec6?style=for-the-badge&logo=react&logoColor=white) ![Vite](https://img.shields.io/badge/Vite-7-007ec6?style=for-the-badge&logo=vite&logoColor=white) ![Supabase](https://img.shields.io/badge/Supabase-PostgreSQL-007ec6?style=for-the-badge&logo=supabase&logoColor=white) ![Status](https://img.shields.io/badge/Status-Protótipo-yellow?style=for-the-badge)

---

## 📚 Índice
- [Links Úteis](#-links-úteis)
- [Sobre o Projeto](#-sobre-o-projeto)
- [Funcionalidades Principais](#-funcionalidades-principais)
- [Tecnologias Utilizadas](#-tecnologias-utilizadas)
- [Arquitetura](#-arquitetura)
- [Instalação e Execução](#-instalação-e-execução)
  - [Pré-requisitos](#pré-requisitos)
  - [Variáveis de Ambiente](#-variáveis-de-ambiente)
  - [Instalação de Dependências](#-instalação-de-dependências)
  - [Banco de Dados (Supabase)](#-banco-de-dados-supabase)
  - [Como Executar a Aplicação](#-como-executar-a-aplicação)
- [Deploy](#-deploy)
- [Estrutura de Pastas](#-estrutura-de-pastas)
- [Demonstração](#-demonstração)
- [Testes](#-testes)
- [Documentações utilizadas](#-documentações-utilizadas)
- [Autores](#-autores)
- [Limitações Conhecidas](#-limitações-conhecidas)
- [Agradecimentos](#-agradecimentos)
- [Licença](#-licença)

---

## 🔗 Links Úteis
* 📦 **Repositório:** <https://github.com/daniell-2508/aluguel-carros>
* 📖 **Modelagem UML (casos de uso, histórias de usuário, classes, estados e pacotes):** [docs/modelagem.md](docs/modelagem.md)
* 🌐 **Demo Online:** não disponível (execução local).

---

## 📝 Sobre o Projeto

Pretende-se apoiar a **gestão de aluguéis de automóveis** pela Internet, permitindo efetuar, cancelar e modificar pedidos. O sistema só pode ser utilizado após cadastro prévio e autenticação.

- **Clientes** (pessoas físicas) introduzem, modificam, consultam e cancelam pedidos de aluguel. Armazenam-se RG, CPF, nome, endereço, profissão e as entidades empregadoras com os respectivos rendimentos (máximo 3).
- **Agentes** (empresas e bancos) modificam e avaliam pedidos. Após a introdução, o pedido é analisado do ponto de vista financeiro e, em caso de parecer positivo, fica disponível para a execução do contrato.
- **Automóveis** registram matrícula, ano, marca, modelo e placa, e podem ser propriedade de clientes, empresas ou bancos.
- O aluguel pode estar associado a um **contrato de crédito** concedido por um banco agente.

O sistema se divide em dois subsistemas: **gestão de pedidos e contratos** (back-end) e **construção dinâmica das páginas web** (front-end React, que monta as telas a partir dos dados da API).

---

## ✨ Funcionalidades Principais

- 🔐 **Autenticação Segura:** cadastro e login com usuário e senha (BCrypt) e token JWT; toda a API, exceto `/api/auth/**`, exige autenticação.
- ⚙️ **CRUD de Cliente:** criação (cadastro), consulta, atualização e exclusão, com até 3 rendimentos.
- 🚘 **Gestão de Automóveis:** cadastro por agentes; consulta por todos os usuários.
- 📋 **Pedidos de Aluguel:** o cliente cria, modifica, consulta o status e cancela; agentes modificam, aprovam ou reprovam (com parecer) e executam o contrato.
- 🏦 **Contrato de Crédito:** bancos podem associar crédito (valor financiado e juros) ao aprovar um pedido.
- ✅ **Regras de Negócio:** período válido, sem conflito de reserva do mesmo veículo, modificação somente em análise e fluxo `EM_ANALISE → APROVADO/REPROVADO → CONTRATADO` (ou `CANCELADO`).
- 🛡️ **Controle de Acesso por Perfil:** cliente só vê os próprios dados e pedidos; ações de agente são bloqueadas para clientes.

---

## 🛠 Tecnologias Utilizadas

### 💻 Front-end

* **Biblioteca:** React 19
* **Linguagem:** JavaScript (ES6+)
* **Roteamento:** React Router 7
* **Estilização:** CSS puro
* **Build Tool:** Vite 7

### 🖥️ Back-end

* **Linguagem:** Java 25 (JDK)
* **Framework:** Spring Boot 4.0.x (Web MVC, Data JPA, Security, Validation)
* **Build e dependências:** Apache Maven
* **Banco de Dados:** Supabase (PostgreSQL); H2 em memória nos perfis `dev` e `test`
* **ORM:** Hibernate/JPA
* **Autenticação:** Spring Security + JWT (jjwt)

---

## 🏗 Arquitetura

O back-end segue a arquitetura **MVC em camadas**; o front-end React atua como **View** e consome a API REST com o token JWT.

- **Model:** entidades JPA (`Usuario`, `Cliente`, `Rendimento`, `Agente`, `Automovel`, `PedidoAluguel`, `ContratoCredito`).
- **Controller:** endpoints REST (`AuthController`, `ClienteController`, `AutomovelController`, `PedidoController`).
- **Service:** regras de negócio e autorização por dono do recurso.
- **Repository:** Spring Data JPA.
- **Padrões:** Repository, Service Layer, DTOs (records) e tratamento global de exceções.
- **Fluxo:** React → `api.js` (Bearer token) → filtro JWT → Controller → Service → Repository → Supabase.
- **Decisões:** autenticação *stateless* com JWT, para o front e o back rodarem separados; cadastro de agentes aberto, para simplificar a demonstração (ver [Limitações](#-limitações-conhecidas)).

### Diagramas

Os diagramas estão em [docs/modelagem.md](docs/modelagem.md) (Mermaid, renderizados pelo GitHub):

| Diagrama | Descrição |
| :---: | :--- |
| **Casos de Uso** | Atores Cliente, Agente e Banco e suas funcionalidades. |
| **Histórias de Usuário** | US01 a US11 com critérios de aceite. |
| **Classes** | Entidades de domínio, enumerações e relacionamentos. |
| **Estados do Pedido** | Transições entre os status do pedido. |
| **Pacotes (Visão Lógica)** | Pacotes do back-end, front-end e banco. |

---

## 🔧 Instalação e Execução

### Pré-requisitos

* **Java JDK:** versão **25**
* **Apache Maven:** 3.9 ou superior
* **Node.js:** versão LTS (18 ou superior) com npm
* **Conta no Supabase** (opcional para testar: o perfil `dev` usa um banco H2 em memória)

---

### 🔑 Variáveis de Ambiente

#### 1 Back-end (Spring Boot)

Defina como variáveis de ambiente do sistema (os valores padrão estão em `backend/src/main/resources/application.properties`). Veja também `backend/.env.example`.

| Variável | Descrição | Exemplo |
| :--- | :--- | :--- |
| `SUPABASE_DB_URL` | URL JDBC do Supabase. | `jdbc:postgresql://db.xxxx.supabase.co:5432/postgres?sslmode=require` |
| `SUPABASE_DB_USER` | Usuário do banco. | `postgres` |
| `SUPABASE_DB_PASSWORD` | Senha do banco. | `sua-senha` |
| `JWT_SECRET` | Chave de assinatura dos tokens (32+ caracteres). | `chave_aleatoria_com_32_ou_mais_caracteres` |
| `JWT_EXPIRATION_MS` | Validade do token em ms (opcional). | `86400000` |
| `CORS_ORIGINS` | Origens permitidas (opcional). | `http://localhost:5173` |

#### 2 Front-end (React, Vite)

Crie `frontend/.env` a partir de `frontend/.env.example`:

| Variável | Descrição | Exemplo |
| :--- | :--- | :--- |
| `VITE_API_URL` | URL base da API. | `http://localhost:8080/api` |

> **Nunca versione credenciais.** `.env` está no `.gitignore`.

---

### 📦 Instalação de Dependências

```bash
git clone https://github.com/daniell-2508/aluguel-carros.git
cd aluguel-carros
```

#### Front-end (React)

```bash
cd frontend
npm install
cd ..
```

#### Back-end (Spring Boot)

```bash
cd backend
mvn clean install
cd ..
```

---

### 💾 Banco de Dados (Supabase)

1. Crie um projeto em <https://supabase.com>.
2. Em *Project Settings → Database → Connection string → JDBC*, copie o host, o usuário e a senha para as variáveis `SUPABASE_DB_*` acima. Se a conexão direta falhar na sua rede, use o endereço do *Session pooler*.
3. **Migrações:** não é preciso executar nada manualmente. O Hibernate cria as tabelas na primeira execução (`spring.jpa.hibernate.ddl-auto=update`): `usuarios`, `clientes`, `cliente_rendimentos`, `agentes`, `automoveis`, `pedidos` e `contratos_credito`.

---

### ⚡ Como Executar a Aplicação

Execute em **dois terminais separados**.

#### Terminal 1: Back-end (Spring Boot)

```bash
cd backend
mvn spring-boot:run
```

Para rodar **sem Supabase** (banco H2 em memória, os dados somem ao parar):

```bash
mvn spring-boot:run "-Dspring-boot.run.profiles=dev"
```
🚀 *O Back-end estará disponível em **http://localhost:8080**.*

#### Terminal 2: Front-end (React, Vite)

```bash
cd frontend
npm run dev
```
🎨 *O Front-end estará disponível em **http://localhost:5173**.*

#### Roteiro rápido de uso

1. Em *Cadastro*, crie um **agente** do tipo Banco e cadastre um automóvel.
2. Cadastre um **cliente** (com rendimentos) e crie um pedido de aluguel.
3. Entre como banco, aprove o pedido (com contrato de crédito) e execute o contrato.
4. Entre como cliente e veja o status `CONTRATADO`.

> A execução com Docker Compose não está configurada neste projeto.

---

## 🚀 Deploy

Build dos dois artefatos separadamente:

```bash
# Front-end (gera frontend/dist com arquivos estáticos)
cd frontend
npm run build

# Back-end (gera o .jar em backend/target)
cd ../backend
mvn clean package
```

Execução em produção (defina antes as variáveis `SUPABASE_DB_*`, `JWT_SECRET`, `CORS_ORIGINS` e `VITE_API_URL`):

```bash
java -jar backend/target/aluguel-carros-1.0.0.jar

npm install -g serve
serve -s frontend/dist
```

---

## 📂 Estrutura de Pastas

```
.
├── .gitignore                   # 🧹 Ignora target, node_modules, .env, etc.
├── README.md                    # 📘 Documentação principal.
├── LICENSE                      # ⚖️ Licença MIT.
│
├── /frontend                    # 📁 Aplicação React + Vite
│   ├── .env.example             # 🧩 Variáveis do Front-end.
│   ├── index.html
│   ├── vite.config.js
│   ├── package.json
│   └── /src
│       ├── main.jsx             # 🚪 Entrada e rotas.
│       ├── api.js               # 🔌 Chamadas HTTP e sessão (JWT).
│       ├── Layout.jsx           # 🧱 Cabeçalho e navegação.
│       ├── styles.css           # 🎨 Estilos.
│       ├── /components          # 🧱 ClienteForm.
│       └── /pages               # 📄 Login, Registro, Pedidos, Automóveis, Clientes.
│
├── /backend                     # 📁 Aplicação Spring Boot
│   ├── .env.example             # 🧩 Variáveis do Back-end.
│   ├── pom.xml                  # 🛠️ Build e dependências.
│   └── /src
│       ├── /main/java/com/aluguel
│       │   ├── /controller      # 🎮 Endpoints REST.
│       │   ├── /service         # ⚙️ Regras de negócio.
│       │   ├── /repository      # 🗄️ Repositórios JPA.
│       │   ├── /model           # 🧬 Entidades.
│       │   ├── /dto             # ✉️ Objetos de entrada e saída.
│       │   ├── /config          # 🔧 Segurança e CORS.
│       │   ├── /security        # 🛡️ JWT.
│       │   └── /exception       # 💥 Tratamento global de erros.
│       ├── /main/resources      # ⚙️ application.properties (+ perfis dev e test).
│       └── /test/java           # 🧪 Testes de integração.
│
└── /docs
    └── modelagem.md             # 📚 Diagramas UML e histórias de usuário.
```

---

## 🎥 Demonstração

> Capturas de tela: _adicionar em `docs/img/` e referenciar aqui após a apresentação._

### 💻 Exemplo de Saída no Terminal (API)

```bash
curl -X POST http://localhost:8080/api/auth/login \
     -H "Content-Type: application/json" \
     -d '{"login":"maria","senha":"123456"}'
```

**Saída Esperada:**
```json
{
  "token": "eyJhbGciOiJIUzM4NCJ9...",
  "login": "maria",
  "perfil": "CLIENTE",
  "nome": "Maria"
}
```

Rota protegida sem token:

```bash
curl -i http://localhost:8080/api/pedidos
# HTTP/1.1 401
```

---

## 🧪 Testes

### Testes de Integração (Back-end)

```bash
cd backend
mvn test
```
*Ferramentas: JUnit 5, Spring Boot Test e MockMvc, com banco H2 em memória.*

Cobertura: acesso sem login (401), cadastro e login, permissões por perfil, criação de pedido, conflito de datas, avaliação com crédito, execução de contrato, bloqueio de cancelamento após contratar e limite de 3 rendimentos.

O front-end não possui testes automatizados.

---

## 🔗 Documentações utilizadas

* 📖 **Spring Boot:** [Documentação Oficial](https://docs.spring.io/spring-boot/docs/current/reference/html/)
* 📖 **Spring Security:** [Documentação Oficial](https://docs.spring.io/spring-security/reference/)
* 📖 **Supabase:** [Documentação Oficial](https://supabase.com/docs)
* 📖 **React:** [Documentação Oficial](https://react.dev/reference/react)
* 📖 **Vite:** [Guia de Configuração](https://vitejs.dev/config/)
* 📖 **Maven:** [Documentação Oficial](https://maven.apache.org/guides/)

---

## 👥 Autores

| 👤 Nome | :octocat: GitHub |
|---------|-----------------|
| Daniell | [daniell-2508](https://github.com/daniell-2508) |

---

## ⚠️ Limitações Conhecidas

- O cadastro de agentes é público para facilitar a demonstração; em produção deveria exigir aprovação ou convite.
- O valor total do pedido é informado manualmente (não há tabela de preços).
- Apenas o back-end possui testes automatizados.
- Não há execução via Docker Compose.

---

## 🙏 Agradecimentos

* [**Engenharia de Software PUC Minas**](https://www.instagram.com/engsoftwarepucminas/) - Pelo apoio institucional e estrutura acadêmica.
* [**Prof. Dr. João Paulo Aramuni**](https://github.com/joaopauloaramuni) - Pelos ensinamentos e pelo material da disciplina (aulas de Spring Boot, templates e exemplos de projetos).

---

## 📄 Licença

Este projeto é distribuído sob a **Licença MIT** (ver [LICENSE](LICENSE)).

---
