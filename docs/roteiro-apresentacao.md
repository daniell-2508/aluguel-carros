# Roteiro de Apresentação — Laboratório 02 (Sistema de Aluguel de Carros)

Duração sugerida: **10 a 12 minutos** + perguntas. Apresentador: Daniell.

O enunciado pede, na apresentação final: (1) mostrar o protótipo, (2) **compará-lo com os modelos descritos inicialmente**, (3) **apresentar as modificações** feitas para o software funcionar, (4) mostrar Spring Boot + MVC, React + Vite, Supabase e login, e (5) repositório GitHub atualizado com os modelos UML e o código final. A avaliação considera: qualidade do sistema, **alinhamento entre modelo e código** e **atualização dos modelos**.

---

## 0. Antes de começar (faça 10 minutos antes)

- [ ] Senha do banco **trocada** no Supabase e anotada.
- [ ] Back-end rodando com o Supabase (log com `Started AluguelApplication`).
- [ ] Front-end rodando em http://localhost:5173.
- [ ] Contas prontas para a demo (crie antes, para não perder tempo):
  - Agente **Banco** (ex.: `banco1`) e agente **Empresa** (ex.: `empresa1`).
  - Cliente (ex.: `cliente1`) com 2 rendimentos.
  - 2 ou 3 automóveis cadastrados.
- [ ] Abas abertas: front (login), Supabase *Table Editor*, GitHub do projeto, `docs/modelagem.md` no GitHub (para os diagramas renderizarem) e a IDE com o projeto.
- [ ] **Plano B:** prints salvos em `docs/img/` caso a internet ou o Supabase falhem.
- [ ] Zoom do navegador em 110–125% para a plateia enxergar.

---

## 1. Mapa: o que o professor pediu → onde está

| Exigência do enunciado | Onde mostrar |
|---|---|
| Java JDK 25, Spring Boot 4.0.x, Maven | `backend/pom.xml` (`java.version` 25, parent `4.0.9`) |
| Arquitetura MVC | `backend/src/main/java/com/aluguel/` — `controller`, `service`, `repository`, `model` |
| React + Vite | `frontend/` (`vite.config.js`, `src/pages`) |
| Supabase (PostgreSQL) | Table Editor com 7 tabelas; `application.properties` com `SUPABASE_DB_URL` |
| Login obrigatório (usuário e senha) | Tela de login; `security/JwtAuthFilter.java`; resposta 401 sem token |
| Cliente: introduzir, modificar, consultar, cancelar pedidos | Tela **Pedidos** como cliente |
| Agentes: modificar e avaliar pedidos | Painel **Avaliar** como banco/empresa |
| Análise financeira → contrato | Botão **Executar contrato** (só pedido `APROVADO`) |
| Dados do contratante: RG, CPF, nome, endereço, profissão, rendimentos (máx. 3) | `ClienteForm.jsx`, `Cliente.java`, validação `@Size(max = 3)` |
| Automóvel: matrícula, ano, marca, modelo, placa; propriedade de cliente/empresa/banco | Tela **Automóveis**; `Automovel.java` + `TipoProprietario` |
| Aluguel associado a contrato de crédito de um banco | `ContratoCredito.java`; checkbox "Conceder contrato de crédito" |
| Dois subsistemas (pedidos/contratos e páginas web dinâmicas) | Back-end (API) e front-end React |
| Diagrama de Casos de Uso, Histórias, Classes, Pacotes | `docs/modelagem.md` |
| Autenticação (clientes e agentes) | `AuthService`, `AuthController` |
| CRUD de cliente (WEB, Spring, React, Supabase, MVC) | Tela **Clientes / Meu cadastro** |
| Protótipo: criar e visualizar status de pedido | Tela **Pedidos** |
| Revisão dos diagramas | Seção 5 deste roteiro e histórico em `docs/modelagem.md` |
| Template de README | `README.md` |
| GitHub com modelos e código final | https://github.com/daniell-2508/aluguel-carros |

---

## 2. Roteiro falado (com tempos)

### 2.1 Abertura — 30 s
> "Nosso sistema apoia a gestão de aluguéis de automóveis pela Internet. Clientes fazem pedidos; empresas e bancos analisam financeiramente, aprovam e executam o contrato, com crédito opcional de um banco. Usamos Spring Boot com MVC no back-end, React com Vite no front-end e Supabase como banco, com login obrigatório."

### 2.2 Modelagem (comparação com o código) — 3 min
Abra `docs/modelagem.md` no GitHub e percorra, sempre ligando o diagrama ao código:

1. **Casos de uso:** três atores (Cliente, Agente, Banco). *"O Banco é um tipo de agente com um caso de uso extra: conceder crédito."*
2. **Histórias de usuário:** US01 a US11, cada uma com critério de aceite. Cite 2 exemplos: US04 (pedido sem conflito de datas) e US06 (só modifica em análise).
3. **Diagrama de classes ↔ pacote `model`:**

| Classe do diagrama | Arquivo no código |
|---|---|
| Usuario | `model/Usuario.java` |
| Cliente, Rendimento | `model/Cliente.java`, `model/Rendimento.java` |
| Agente | `model/Agente.java` |
| Automovel | `model/Automovel.java` |
| PedidoAluguel | `model/PedidoAluguel.java` |
| ContratoCredito | `model/ContratoCredito.java` |
| Perfil, StatusPedido, TipoProprietario | enums em `model/` |

4. **Estados do pedido:** `EM_ANALISE → APROVADO/REPROVADO → CONTRATADO`, e `CANCELADO`. Está implementado em `service/PedidoService.java`.
5. **Diagrama de pacotes:** mostre que bate com as pastas reais (`controller`, `service`, `repository`, `model`, `dto`, `security`, `config`, `exception`).

### 2.3 Arquitetura MVC — 1 min 30 s
Na IDE, abra a árvore `com.aluguel`:
- **Model:** entidades JPA.
- **View:** React (o front consome a API REST).
- **Controller:** `controller/` só recebe a requisição e delega.
- **Service:** regras de negócio (conflito de datas, estados, permissões por dono).
- **Repository:** Spring Data JPA.
- Mencione **DTOs** (`dto/`), **tratamento global de erros** (`exception/`) e **JWT** (`security/`).

### 2.4 Demonstração ao vivo — 4 min
Siga esta ordem (cada passo mostra um requisito):

| # | Ação | O que dizer |
|---|---|---|
| 1 | Tente abrir `/pedidos` sem login → volta ao login | "Só usa quem está cadastrado e autenticado." |
| 2 | Entre como **cliente1** | Login com usuário e senha. |
| 3 | **Meu cadastro:** mostre os dados e edite um campo | CRUD de cliente; até 3 rendimentos. |
| 4 | **Pedidos → novo pedido** (carro, datas, valor) | Status nasce `EM ANALISE`. |
| 5 | Repita o mesmo carro nas mesmas datas | Erro de conflito: regra de negócio. |
| 6 | **Modificar** o pedido e salvar | Só permitido em análise. |
| 7 | Saia; entre como **banco1** | Perfil agente. |
| 8 | **Avaliar:** parecer + marcar crédito (valor e juros) → **Aprovar** | Análise financeira e contrato de crédito. |
| 9 | **Executar contrato** | Status `CONTRATADO`. |
| 10 | Volte como cliente: veja o status e tente **Cancelar** | Pedido contratado não cancela. |
| 11 | (Opcional) Entre como **empresa1** e tente aprovar com crédito | Só banco concede crédito. |

### 2.5 Banco de dados (Supabase) — 1 min
Table Editor: mostre `usuarios` (coluna `senha` com hash `$2a$...`, BCrypt), `clientes`, `cliente_rendimentos`, `pedidos` e `contratos_credito` com os dados criados na demo. Diga que o Hibernate cria as tabelas e que a conexão usa variáveis de ambiente (nada de senha no repositório).

### 2.6 Testes e repositório — 1 min
- Terminal: `mvn test` (3 testes de integração, todos passando). Eles cobrem: 401 sem login, permissões por perfil, conflito de datas, avaliação com crédito, contrato, bloqueio de cancelamento e limite de 3 rendimentos.
- GitHub: mostre `README.md` (template da disciplina), `docs/modelagem.md` e o histórico de commits.

---

## 3. Comparação modelo × protótipo: modificações feitas

Este é o ponto que o enunciado mais cobra. Use a tabela e **só afirme o que reconhece como verdade do seu processo**.

| Tema | No modelo inicial | No sistema final | Motivo |
|---|---|---|---|
| Autenticação | Cliente com dados de login | `Usuario` separado, ligado a `Cliente` ou `Agente`, com `Perfil` | Um único login para clientes e agentes, com perfis |
| Agentes | Empresas e bancos | Entidade `Agente` (nome, CNPJ); o tipo vem do `Perfil` (EMPRESA/BANCO) | Evitar duas classes quase iguais |
| Rendimentos | Atributo do cliente | `Rendimento` embutido (até 3), validado no back-end | Respeitar o máximo de 3 do enunciado |
| Contrato de crédito | Associado ao aluguel | `ContratoCredito` ligado ao pedido e a um agente do tipo BANCO | Só bancos concedem crédito |
| Estados do pedido | Análise e execução | `EM_ANALISE, APROVADO, REPROVADO, CANCELADO, CONTRATADO` | Tornar o fluxo explícito e testável |
| Conflito de reservas | Não previsto | Consulta que bloqueia o mesmo carro em períodos sobrepostos | Evitar reserva duplicada |
| Segurança | Login obrigatório | JWT + BCrypt + autorização por perfil e por dono do recurso | Cliente só vê o que é seu |
| Avaliação na tela | Pop-ups do navegador (`prompt`) | Painel "Avaliar" na própria página | Os pop-ups falhavam (ações sem resposta visível) |
| Banco de testes | — | Perfil `dev`/`test` com H2 | Rodar sem Supabase |

> **Honestidade:** hoje o repositório guarda **apenas a versão atual (1.1)** dos modelos. O enunciado pede "todas as versões produzidas". Se existirem versões anteriores dos seus diagramas (rascunhos, prints, arquivos antigos), **coloque-as em `docs/versoes/`** antes de entregar. Daqui em diante, ao mudar um diagrama, salve a versão antiga antes (ex.: `docs/versoes/modelagem-v1.1.md`) e atualize o histórico no final de `docs/modelagem.md`.

### Pontos em que o código simplifica o modelo (diga antes que perguntem)
- **Proprietário do automóvel** é só um *tipo* (cliente, empresa ou banco), não uma ligação com o dono específico.
- **Valor total do pedido** é digitado, não calculado (não há tabela de diárias).
- **Cadastro de agentes é público**, para facilitar a demo; em produção exigiria aprovação.
- **Front-end sem testes automatizados**; só o back-end tem.

---

## 4. Perguntas prováveis e respostas

**Por que MVC se o front é React?**
O back-end segue MVC (Model = entidades, Controller = endpoints, Service/Repository como apoio) e o React é a View, desacoplada e consumindo a API REST.

**Como funciona o login?**
`POST /api/auth/login` valida usuário e senha (BCrypt) e devolve um JWT. O front envia o token em cada requisição; o `JwtAuthFilter` autentica e o Spring Security aplica as permissões. Sem token, a resposta é 401.

**Como garante que um cliente não veja pedido de outro?**
O `PedidoService` compara o login do token com o dono do pedido; se não for agente e não for o dono, retorna 403.

**Onde está o limite de 3 rendimentos?**
`ClienteRequest` tem `@Size(max = 3)`, e o formulário também desabilita o botão ao chegar em 3.

**Quem pode conceder crédito?**
Só agente com perfil BANCO; se uma empresa tentar, o back-end recusa.

**O que acontece se dois clientes pedirem o mesmo carro nas mesmas datas?**
O segundo pedido é recusado pela consulta de conflito (`existeConflito`).

**Por que Supabase e como conecta?**
É um PostgreSQL gerenciado. Conectamos por JDBC via *Session pooler*, com URL, usuário e senha em variáveis de ambiente.

**Como roda o projeto?**
Seção "Instalação e Execução" do README: variáveis de ambiente, `mvn spring-boot:run` e `npm run dev`.

**O que faria com mais tempo?**
Cálculo automático do valor por diária, vínculo do carro com o proprietário, aprovação de novos agentes, testes do front-end e Docker Compose.

---

## 5. Plano B (se algo falhar na hora)

| Problema | Solução |
|---|---|
| Sem internet ou Supabase fora | Suba o back-end com o perfil dev: `mvn spring-boot:run "-Dspring-boot.run.profiles=dev"` e refaça os cadastros rápidos; mostre os prints do Supabase. |
| Porta 8080 ocupada | Encerre o processo antigo: `netstat -ano \| findstr :8080` e depois `Stop-Process -Id <PID>`. |
| Variáveis de ambiente perdidas | Redefina as 4 variáveis (URL, usuário, `JWT_SECRET`, senha) na mesma janela. |
| Tela em branco no front | Atualize com F5 e confira se `npm run dev` está ativo. |
| Erro inesperado na demo | Mostre o log do back-end e os prints; explique o fluxo pelo diagrama de estados. |

---

## 6. Checklist final de entrega

- [ ] README no template da disciplina, com seu nome em *Autores*.
- [ ] `docs/modelagem.md` atualizado e, se houver, versões antigas em `docs/versoes/`.
- [ ] Prints das telas em `docs/img/` (e referenciados no README, seção *Demonstração*).
- [ ] Nenhuma senha ou `.env` no repositório (`git log` e `git status` limpos de segredos).
- [ ] `git push` feito; abra o GitHub e confira que `backend`, `frontend` e `docs` aparecem.
- [ ] Repositório acessível ao professor (público ou com ele como colaborador).
- [ ] Senha do banco trocada depois da apresentação, se ela foi exposta.
