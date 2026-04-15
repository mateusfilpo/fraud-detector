# Fraud Detector — Detecção de Fraude em Transações com Grafos

> Sistema que detecta fraudes em transações financeiras usando **grafos (Neo4j)**.
> Fraude é um problema de *relacionamentos* — grafos resolvem com elegância onde SQL
> exigiria JOINs pesados.

## Stack

- **Java 25** + **Spring Boot 4.0**
- **Spring Data Neo4j** + **Neo4j 5**
- **Arquitetura Hexagonal** (Ports & Adapters)
- **Testcontainers** com Neo4j real
- **Swagger UI** para documentação da API

## Padrões de Fraude Detectados

| Padrão | Descrição | Score |
|---|---|---|
| 🔄 Fraud Ring | Dinheiro circula entre contas e volta à origem | 40 |
| 🍊 Orange Account | Recebe de muitas fontes, repassa para uma única | 35 |
| 🌍 Impossible Travel | Transações em locais impossíveis no tempo | 30 |
| 📱 Shared Device | Contas "sem relação" no mesmo dispositivo | 25 |
| 🏪 Suspicious Merchant | Merchant recebe de muitas contas novas | 20 |

## Arquitetura

```
Controllers (REST API)
       ↓
   Services (Application Layer)
       ↓
    Domain (Models, Ports, Exceptions)
       ↑
  Adapters (Neo4j Persistence + Cypher Queries)
```

## Como Rodar

```bash
# 1. Clonar
git clone https://github.com/mateusfilpo/fraud-detector.git
cd fraud-detector

# 2. Subir (Docker Compose + Neo4j automático)
./mvnw spring-boot:run

# 3. Acessar
# API:     http://localhost:8080/api/accounts
# Swagger: http://localhost:8080/swagger-ui.html
```

## Endpoints

| Método | Path | Descrição |
|---|---|---|
| POST | `/api/accounts` | Criar conta |
| GET | `/api/accounts` | Listar contas |
| POST | `/api/transactions` | Criar transação (dispara detecção automática) |
| GET | `/api/transactions/{id}/fraud-score` | Score de fraude |
| GET | `/api/alerts` | Listar alertas |
| GET | `/api/alerts/status/{status}` | Filtrar alertas por status |
| PATCH | `/api/alerts/{id}/review` | Marcar em revisão |
| PATCH | `/api/alerts/{id}/confirm` | Confirmar fraude |

## Testes

```bash
# Testes de integração com Neo4j real via Testcontainers
./mvnw test
```

## Autor

**Mateus Filpo** — [GitHub](https://github.com/mateusfilpo)