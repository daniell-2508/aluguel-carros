# Sistema de Aluguel de Carros

Laboratório 02 — DIAW (Engenharia de Software). Sistema web para efetuar, modificar, consultar, cancelar e avaliar pedidos de aluguel de automóveis, com login obrigatório.

## Stack

| Camada | Tecnologia |
|--------|-----------|
| Linguagem | Java (JDK 25) |
| Back-end | Spring Boot 4.0.x, arquitetura MVC, Spring Security + JWT |
| Build | Apache Maven |
| Front-end | React + Vite |
| Banco de dados | Supabase (PostgreSQL) |
| Autenticação | Usuário e senha (BCrypt), token JWT |

## Estrutura

```
aluguel-carros/
├── backend/    API Spring Boot (controller, service, repository, model, dto, security)
├── frontend/   React + Vite
└── docs/       Modelagem UML (casos de uso, histórias, classes, pacotes)
```

Modelos: [docs/modelagem.md](docs/modelagem.md).

## Funcionalidades

- **Autenticação:** cadastro e login de clientes e agentes (empresa/banco); toda a API, exceto `/api/auth/**`, exige token.
- **CRUD de cliente:** RG, CPF, nome, endereço, profissão e até 3 rendimentos (entidade empregadora + valor).
- **Automóveis:** matrícula, ano, marca, modelo, placa e tipo de proprietário (cliente, empresa ou banco). Agentes mantêm o cadastro.
- **Pedidos de aluguel:** cliente cria, modifica, consulta e cancela; agentes modificam e avaliam (aprovar/reprovar com parecer) e executam o contrato. Bancos podem associar um contrato de crédito.
- **Regras:** período válido, sem conflito de reserva do mesmo veículo, modificação só em análise, fluxo de status `EM_ANALISE → APROVADO/REPROVADO → CONTRATADO` (ou `CANCELADO`).

## Configuração do Supabase

1. Crie um projeto em <https://supabase.com> e copie os dados de conexão (*Project Settings → Database → Connection string → JDBC*).
2. Defina as variáveis de ambiente antes de subir o back-end:

```bash
SUPABASE_DB_URL=jdbc:postgresql://<host>:5432/postgres?sslmode=require
SUPABASE_DB_USER=<usuario>
SUPABASE_DB_PASSWORD=<senha>
JWT_SECRET=<chave aleatória com 32+ caracteres>
```

As tabelas são criadas automaticamente pelo Hibernate (`ddl-auto=update`). **Nunca versione credenciais.**

## Como executar

Back-end (porta 8080):

```bash
cd backend
mvn spring-boot:run                                    # usa o Supabase
mvn spring-boot:run -Dspring-boot.run.profiles=dev     # banco H2 em memória, sem Supabase
mvn test                                               # testes de integração
```

Front-end (porta 5173):

```bash
cd frontend
cp .env.example .env     # ajuste VITE_API_URL se necessário
npm install
npm run dev
```

## Roteiro de demonstração

1. Em `/registro`, cadastre um **agente** do tipo Banco e cadastre um automóvel.
2. Cadastre um **cliente** (com rendimentos) e crie um pedido de aluguel.
3. Entre como banco, aprove o pedido (com contrato de crédito) e execute o contrato.
4. Entre como cliente e veja o status `CONTRATADO`.

## Endpoints principais

| Método | Rota | Perfil |
|--------|------|--------|
| POST | `/api/auth/login`, `/api/auth/registro/cliente`, `/api/auth/registro/agente` | público |
| GET/PUT/DELETE | `/api/clientes/{id}`, GET `/api/clientes/me`, GET `/api/clientes` (agentes) | autenticado |
| GET/POST/PUT/DELETE | `/api/automoveis` | leitura: todos; escrita: agentes |
| POST/GET/PUT | `/api/pedidos`, `/api/pedidos/{id}` | cliente cria; ambos modificam |
| POST | `/api/pedidos/{id}/cancelar` | cliente/agente |
| POST | `/api/pedidos/{id}/avaliacao`, `/api/pedidos/{id}/contrato` | agentes |

## Limitações conhecidas

- O cadastro de agentes é público para facilitar a demonstração; em produção deveria exigir aprovação/convite.
- O valor total do pedido é informado manualmente (não há tabela de preços/diárias).
- Apenas o back-end tem testes automatizados.
