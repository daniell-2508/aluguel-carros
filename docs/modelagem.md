# Modelagem do Sistema de Aluguel de Carros

Diagramas em Mermaid (renderizados automaticamente pelo GitHub). Versão **1.1** — ver histórico no final.

## 1. Diagrama de Casos de Uso

```mermaid
flowchart LR
    Cliente((Cliente))
    Agente((Agente<br/>Empresa / Banco))
    Banco((Banco))
    Agente --- Banco

    subgraph Sistema[Sistema de Aluguel de Carros]
        UC1([Cadastrar-se])
        UC2([Autenticar-se - login])
        UC3([Gerenciar cadastro - CRUD cliente])
        UC4([Introduzir pedido de aluguel])
        UC5([Modificar pedido])
        UC6([Consultar pedidos / status])
        UC7([Cancelar pedido])
        UC8([Avaliar pedido financeiramente])
        UC9([Executar contrato])
        UC10([Gerenciar automóveis])
        UC11([Conceder contrato de crédito])
    end

    Cliente --- UC1 & UC2 & UC3 & UC4 & UC5 & UC6 & UC7
    Agente --- UC1 & UC2 & UC5 & UC6 & UC8 & UC9 & UC10 & UC3
    Banco --- UC11
    UC4 -. include .-> UC2
    UC8 -. extend .-> UC11
    UC9 -. include .-> UC8
```

## 2. Histórias de Usuário

| ID | Como... | Quero... | Para... | Critérios de aceite |
|----|---------|----------|---------|---------------------|
| US01 | visitante | me cadastrar com usuário e senha, informando RG, CPF, nome, endereço, profissão e até 3 rendimentos | poder usar o sistema | CPF/RG/login únicos; senha ≥ 6 caracteres; máximo de 3 rendimentos |
| US02 | usuário cadastrado | fazer login com usuário e senha | acessar as funções do meu perfil | sem login, a API responde 401; senha inválida é rejeitada |
| US03 | cliente | consultar, alterar e excluir meu cadastro | manter meus dados atualizados | só acesso ao próprio cadastro; exclusão bloqueada se houver pedidos |
| US04 | cliente | introduzir um pedido de aluguel de um automóvel | alugar o veículo | período válido, sem conflito com outro pedido ativo; nasce `EM_ANALISE` |
| US05 | cliente | consultar o status dos meus pedidos | acompanhar a análise | lista somente meus pedidos |
| US06 | cliente ou agente | modificar um pedido | corrigir datas, veículo ou valor | somente em `EM_ANALISE` |
| US07 | cliente ou agente | cancelar um pedido | desistir do aluguel | somente em `EM_ANALISE` ou `APROVADO` |
| US08 | agente | avaliar financeiramente um pedido | aprovar ou reprovar | registra avaliador e parecer; só em `EM_ANALISE` |
| US09 | banco | conceder contrato de crédito ao aprovar | financiar o aluguel | apenas perfil `BANCO`; informa valor financiado e juros |
| US10 | agente | executar o contrato de um pedido aprovado | formalizar o aluguel | só em `APROVADO`; passa a `CONTRATADO` |
| US11 | agente | cadastrar e manter automóveis (matrícula, ano, marca, modelo, placa, proprietário) | disponibilizar frota | placa e matrícula únicas; proprietário = cliente, empresa ou banco |

## 3. Diagrama de Classes

```mermaid
classDiagram
    class Usuario {
        +Long id
        +String login
        +String senha
        +Perfil perfil
    }
    class Cliente {
        +Long id
        +String nome
        +String rg
        +String cpf
        +String endereco
        +String profissao
    }
    class Rendimento {
        +String entidadeEmpregadora
        +BigDecimal valor
    }
    class Agente {
        +Long id
        +String nome
        +String cnpj
    }
    class Automovel {
        +Long id
        +String matricula
        +Integer ano
        +String marca
        +String modelo
        +String placa
        +TipoProprietario tipoProprietario
    }
    class PedidoAluguel {
        +Long id
        +LocalDate dataInicio
        +LocalDate dataFim
        +BigDecimal valorTotal
        +StatusPedido status
        +String parecer
        +LocalDateTime criadoEm
    }
    class ContratoCredito {
        +Long id
        +BigDecimal valorFinanciado
        +BigDecimal taxaJurosMensal
    }
    class Perfil { <<enumeration>> CLIENTE EMPRESA BANCO }
    class StatusPedido { <<enumeration>> EM_ANALISE APROVADO REPROVADO CANCELADO CONTRATADO }
    class TipoProprietario { <<enumeration>> CLIENTE EMPRESA BANCO }

    Usuario "1" -- "0..1" Cliente
    Usuario "1" -- "0..1" Agente
    Cliente "1" *-- "0..3" Rendimento
    Cliente "1" -- "0..*" PedidoAluguel : solicita
    Automovel "1" -- "0..*" PedidoAluguel : alugado em
    Agente "1" -- "0..*" PedidoAluguel : avalia
    PedidoAluguel "1" *-- "0..1" ContratoCredito
    Agente "1" -- "0..*" ContratoCredito : banco concedente
    Usuario --> Perfil
    PedidoAluguel --> StatusPedido
    Automovel --> TipoProprietario
```

### Estados do pedido

```mermaid
stateDiagram-v2
    [*] --> EM_ANALISE : cliente introduz
    EM_ANALISE --> EM_ANALISE : modificar
    EM_ANALISE --> APROVADO : agente aprova
    EM_ANALISE --> REPROVADO : agente reprova
    APROVADO --> CONTRATADO : agente executa contrato
    EM_ANALISE --> CANCELADO
    APROVADO --> CANCELADO
```

## 4. Diagrama de Pacotes (Visão Lógica) e Arquitetura MVC

```mermaid
flowchart TB
    subgraph FE[frontend - React + Vite  -  View]
        pages[pages / components]
        apijs[api.js - cliente HTTP + JWT]
    end
    subgraph BE[backend - Spring Boot - arquitetura MVC]
        controller[com.aluguel.controller<br/>Controller]
        dto[com.aluguel.dto]
        service[com.aluguel.service<br/>regras de negócio]
        model[com.aluguel.model<br/>Model - entidades JPA]
        repository[com.aluguel.repository]
        security[com.aluguel.security + config<br/>JWT / Spring Security]
        exception[com.aluguel.exception]
    end
    DB[(Supabase - PostgreSQL)]

    pages --> apijs -->|REST / JSON + Bearer token| security
    security --> controller
    controller --> dto
    controller --> service
    service --> repository
    service --> model
    repository --> model
    repository -->|JDBC| DB
    controller -.-> exception
```

Os dois subsistemas do enunciado: **gestão de pedidos e contratos** (pacotes `service`/`controller`/`model` do back-end) e **construção dinâmica das páginas web** (front-end React, que monta as telas a partir dos dados da API).

## 5. Histórico de versões dos modelos

| Versão | Mudança |
|--------|---------|
| 1.0 | Modelo inicial: Usuario, Cliente, Automovel, PedidoAluguel, casos de uso básicos. |
| 1.1 | Inclusão de `Agente` (empresa/banco), `Rendimento` (máx. 3), `ContratoCredito`, estados do pedido e `TipoProprietario`; autenticação JWT no pacote `security`. |
