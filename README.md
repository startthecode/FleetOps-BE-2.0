# FleetOps

A logistics & delivery management platform built on **microservices**, **event-driven architecture**, and **agentic AI**.

FleetOps covers the full order lifecycle — from placement to final delivery — across customers, vendors, warehouse staff, drivers, and admins. Services are independently deployable and communicate over both **synchronous** (REST / Spring `WebClient`) and **asynchronous** (Kafka + Avro + Schema Registry) channels, fronted by an API Gateway and enhanced with AI-driven support, dispatch, and admin-ops agents.

> This README describes the full target architecture; see [Implementation status](#implementation-status) for what is delivered today versus in progress.

## Platform capabilities

- Event-driven architecture with **Kafka + Avro + Schema Registry** (with schema evolution)
- Synchronous inter-service calls with **Spring WebClient**
- **API Gateway** routing, filtering, and auth enforcement
- **Redis** for caching, distributed locking, idempotency, and rate limiting
- **Docker / Docker Compose** multi-service orchestration
- **Agentic AI** — tool-calling, RAG, and LLM-orchestrated workflows within the service mesh
- Enterprise patterns: **Saga, Outbox, Circuit Breaker, CQRS-lite** read models

## Target architecture

| Service | Responsibility | Key tech |
|---|---|---|
| **API Gateway** | Entry point; JWT validation, role-based routing, Redis rate limiting | Spring Cloud Gateway, Redis |
| **Discovery Server** | Service registry | Eureka |
| **Config Server** | Centralized externalized config | Spring Cloud Config |
| **Customer Service** | Auth, profiles, addresses | REST, JWT |
| **Order Service** | Order lifecycle orchestration (Saga, Outbox) | WebClient, Kafka producer |
| **Inventory Service** | Stock levels, reservation, oversell prevention | Redis distributed lock, Kafka |
| **Driver Service** | Driver profiles, availability | Kafka |
| **Tracking Service** | Live status, location, history | Kafka, WebSocket, Redis |
| **Notification Service** | SMS / email / push alerts | Kafka, Redis idempotency |
| **Analytics Service** | Aggregated dashboards / reporting | Kafka, read-model DB |
| **AI Support Agent** | Conversational customer support via tool-calling + RAG | LLM API, function calling |
| **Dispatch & ETA Agent** | Driver assignment + delivery-time prediction | Rule engine + LLM reasoning, Redis geo |
| **Admin Ops Agent** | NL-driven CRUD/debugging for admins; writes gated by confirmation | LLM API, write-scoped tools |

### AI agents

Three autonomous system actors, each constrained to act only through tools:

- **Support Agent** — answers customer order questions (`getOrderStatus`, `getETA`, `cancelOrder`, …), uses RAG for policy questions, scoped to the requesting customer, and escalates to a human when confidence is low or an action is destructive.
- **Dispatch & ETA Agent** — scores drivers on proximity, load, priority, and on-time history; uses LLM reasoning only to break near-ties, logging a reasoning trace. Continuously predicts ETAs.
- **Admin Ops Agent** — internal, ADMIN-only. Read tools run immediately; every mutating tool call is a *proposal* requiring explicit confirmation, enforces the same RBAC as the REST API, has a bounded per-action blast radius, and never touches a datastore directly.

## Event-driven design

Services publish/consume Avro events validated against the Schema Registry. Producers use the **transactional outbox** pattern (event persisted atomically with state change, then relayed to Kafka); consumers use **idempotent** handling with retry + dead-letter topics.

### Event catalog (target)

| Event | Producer | Consumers | Key fields |
|---|---|---|---|
| `OrderCreated` | Order | Inventory, Notification, Analytics, Dispatch | orderId, customerId, items[], warehouseId, priority |
| `OrderStatusChanged` | Order | Tracking, Notification, Analytics | orderId, oldStatus, newStatus, timestamp |
| `OrderCancelled` | Order | Inventory, Driver, Notification | orderId, reason |
| `StockAdjusted` | Inventory | Analytics | sku, warehouseId, delta |
| `DriverAssigned` | Dispatch | Tracking, Notification, Driver | orderId, driverId, assignmentScore |
| `DriverReassigned` | Dispatch | Tracking, Notification | orderId, oldDriverId, newDriverId, reason |
| `ETAUpdated` | Dispatch | Tracking, Notification | orderId, newEta, deltaMinutes |
| `SLABreach` | Analytics / Dispatch | Notification, Analytics | driverId, orderId, delayMinutes |

**Schema evolution:** new fields are introduced as optional with defaults and validated under the registry's `BACKWARD` compatibility mode, so consumers on older schema versions keep working across releases.

## Implementation status

The repository currently implements an early slice of the platform:

| Module | State | Notes |
|---|---|---|
| `common-library` | ✅ | Shared enums/constants (`KafkaTopics`, `Routes`, `ReqHeadersKeys`, `OutboxStatus`, `InboxStatus`) |
| `kafka-avro-schemas` | ✅ | Avro `EventEnvelope` + product events; generates event classes |
| `user-service` (:1000) | ✅ | Auth + JWT issuance (RSA private key) — the FRD's *Customer Service* |
| `product-service` (:1001) | ✅ | CRUD + Kafka **outbox** publishing Avro `product.*` events — precursor to *Order/Inventory* |
| `notification-service` (:1002) | ✅ | Kafka consumer → email (Resend), retry + DLT, inbox scaffold |
| `api-gateway` (:8888) | 🟡 | JWT validation (RSA public key); RBAC routing + Redis rate limiting planned |
| `tracking-service` | ⬜ | Module scaffold only |
| Order / Inventory / Driver / Dispatch / Analytics / AI agents / Discovery / Config | ⬜ | Planned (see [Roadmap](#roadmap)) |

Legend: ✅ implemented · 🟡 partial · ⬜ planned

## Tech stack

- **Java 21**, **Spring Boot 4.1**
- **Apache Kafka 4.x** + **Confluent Schema Registry** + **Avro**
- **PostgreSQL 17**, **Redis 8**
- **JWT** (RS256) — signed by user-service, verified at the gateway
- **Resend** for transactional email
- **Maven** — each service is a standalone module; shared modules install to the local repo

## Getting started

### 1. Start infrastructure

```bash
docker compose up -d
```

Brings up PostgreSQL, Redis, Kafka, Schema Registry, and Kafka-UI. Host ports come from the root `.env` (`POSTGRES_PORT`, `KAFKA_PORT`, `SCHEMA_REGISTRY_PORT`, `KAFKA_UI_PORT`).

### 2. Configure each service

Each service reads its own `<service>/.env` (via `spring.config.import`): DB credentials, `KAFKA_BOOTSTRAP_SERVER`, `AVRO_REGISTRY_URL`, and — for notification-service — Resend:

```
RESEND_API_KEY=<your key>
NOTIFICATION_MAIL_FROM=<a verified Resend sender>
```

> `.env` files and `*.pem` key material are git-ignored — never commit them.

### 3. Generate the JWT RSA keypair

user-service needs the **private** key, the gateway the **public** key (each under `src/main/resources/keys/`, git-ignored):

```bash
openssl genrsa -out private.pem 2048
openssl rsa -in private.pem -pubout -out public.pem
```

### 4. Build shared modules, then run services

```bash
mvn -f kafka-avro-schemas/pom.xml install
mvn -f common-library/pom.xml install
```

Then start each service from its module (IDE run config or `mvn spring-boot:run`). Typical order: user-service → product-service → notification-service → api-gateway.

## Roadmap

1. **Core REST + WebClient** — Customer, Order, Inventory (Redis lock); no Kafka yet.
2. **Event backbone** — Kafka + Avro + Schema Registry; Outbox pattern; first producer/consumers.
3. **Driver + Tracking + WebSockets** — live location via Redis, Tracking cache.
4. **Dispatch & ETA Agent** — rule-based scoring first, then LLM tie-breaking.
5. **AI Support Agent** — tool-calling over existing REST endpoints, then RAG knowledge base.
6. **Admin Ops Agent** — read-only debugging tools first, then confirm-before-execute writes.
7. **Gateway + Discovery + Config Server** — routing, Redis rate limiting, JWT enforcement.
8. **Analytics** — read models off existing event streams.
9. **Hardening** — Resilience4j, distributed tracing (OpenTelemetry + Jaeger), chaos testing, Saga compensation.

## Non-functional goals

- Each service independently deployable via its own Dockerfile; full stack via a single `docker-compose.yml`.
- All async messaging is Avro, validated against the Schema Registry.
- Distributed tracing across REST and Kafka boundaries (OpenTelemetry + Zipkin/Jaeger).
- AI responses bounded by a hard timeout with graceful fallback.
- Every destructive agent action is reversible or requires confirmation; the Admin Ops Agent has **no standing write access**.

## Repository layout

```
fleetOps-v2/
├── api-gateway/          # JWT-validating gateway (WebFlux)
├── user-service/         # auth + JWT issuance
├── product-service/      # CRUD + Kafka outbox
├── notification-service/ # Kafka consumer + email (Resend)
├── tracking-service/     # planned
├── common-library/       # shared enums/constants
├── kafka-avro-schemas/   # Avro schemas + generated event classes
└── docker-compose.yml    # Kafka, Schema Registry, Postgres, Redis, Kafka-UI
```
