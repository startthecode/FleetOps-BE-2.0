# FleetOps — Functional Requirements Document

**Version:** 1.1
**Type:** Learning / Portfolio Project — Microservices, Event-Driven Architecture, AI Agents
**Domain:** Logistics & Delivery Management Platform

---

## 1. Project Overview

FleetOps is a logistics and delivery management platform covering the full order lifecycle — from order placement to final delivery — across multiple actors (customers, vendors, warehouse staff, drivers, admins). The platform is built as a set of independently deployable microservices communicating through both synchronous (REST/WebClient) and asynchronous (Kafka/Avro) channels, fronted by an API Gateway, and enhanced with two AI-driven components: a conversational support agent and an intelligent dispatch/ETA engine.

### 1.1 Learning Objectives
This project exists to build hands-on depth in:
- Event-driven architecture with **Kafka + Avro + Schema Registry** (including schema evolution)
- Synchronous inter-service calls with **Spring WebClient**
- **API Gateway** routing, filtering, and auth enforcement
- **Redis** for caching, distributed locking, idempotency, and rate limiting
- **Docker / Docker Compose** multi-service orchestration
- **Agentic AI integration** — tool-calling, RAG, and LLM-orchestrated workflows inside a microservice architecture
- Supporting enterprise patterns: Saga, Outbox, Circuit Breaker, CQRS-lite read models

---

## 2. Actors / Roles

| Role | Description |
|---|---|
| **Customer** | Places orders, tracks shipments, interacts with AI support agent |
| **Vendor** | Manages product catalog and stock at their warehouse(s) |
| **Warehouse Manager** | Confirms stock reservation, packs and hands off orders |
| **Driver** | Accepts assignments, updates delivery status, shares live location |
| **Admin** | Manages users, views analytics, overrides disputes |
| **AI Support Agent** | Autonomous system actor — handles customer queries via tool-calling |
| **Dispatch Agent** | Autonomous system actor — assigns drivers and predicts ETAs |
| **Admin Ops Agent** | Autonomous system actor — assists admins with CRUD operations and debugging via natural language, all writes gated behind confirmation |

---

## 3. System Architecture

| Service | Responsibility | Key Tech |
|---|---|---|
| API Gateway | Entry point, JWT validation, role-based routing, Redis rate limiting | Spring Cloud Gateway, Redis |
| Discovery Server | Service registry | Eureka |
| Config Server | Centralized externalized config | Spring Cloud Config |
| Customer Service | Auth, profiles, addresses | REST, JWT |
| Order Service | Order lifecycle orchestration | WebClient, Kafka producer |
| Inventory Service | Stock levels, reservation, oversell prevention | Redis distributed lock, Kafka consumer |
| Driver Service | Driver profiles, availability | Kafka consumer/producer |
| Tracking Service | Live status, location, history | Kafka consumer, WebSocket, Redis cache |
| Notification Service | SMS/email/push alerts | Kafka consumer, Redis idempotency |
| Analytics Service | Aggregated dashboards, reporting | Kafka consumer, read-model DB |
| **AI Support Agent Service** | Conversational customer support with tool-calling | LLM API, RAG, function calling |
| **Dispatch & ETA Agent Service** | Driver assignment + delivery time prediction | Rule engine + LLM reasoning, Redis geo data |
| **Admin Ops Agent Service** | Internal agent for CRUD operations and system debugging via natural language | LLM API, write-scoped tool calling, event-trace retrieval |

---

## 4. Functional Requirements

### 4.1 Order Service

- **FR-101**: System shall allow a customer to place an order containing multiple line items, each referencing a `productId`, `quantity`, and target `warehouseId`.
- **FR-102**: Before confirming an order, Order Service shall call Inventory Service **synchronously via WebClient** to verify stock availability for every line item.
- **FR-103**: If any line item is unavailable, the system shall support **partial order confirmation** — confirming available items and marking unavailable ones as `BACKORDERED`, subject to customer opt-in at checkout.
- **FR-104**: On successful stock check, Order Service shall persist the order using the **Outbox pattern** (write order + outbox row in the same DB transaction) and a separate poller/CDC process shall publish the `OrderCreated` Avro event to Kafka — preventing dual-write inconsistency.
- **FR-105**: Order status shall follow the state machine:
  `CREATED → CONFIRMED → PACKED → ASSIGNED → PICKED_UP → IN_TRANSIT → DELIVERED`
  with side branches to `CANCELLED` and `RETURNED`.
- **FR-106**: Customers may cancel an order only while in `CREATED`, `CONFIRMED`, or `PACKED` states. Cancellation after `ASSIGNED` requires driver/admin approval and triggers a compensating **Saga** to release reserved stock and unassign the driver.
- **FR-107**: Each state transition shall publish an `OrderStatusChanged` Avro event consumed independently by Tracking, Notification, and Analytics services.
- **FR-108**: System shall support **priority orders** (e.g., express delivery) that are weighted higher in the Dispatch Agent's assignment logic.
- **FR-109**: If Inventory Service does not respond within a configured timeout, Order Service shall apply a **circuit breaker (Resilience4j)** and fall back to a "reservation pending" state rather than failing the request outright.

### 4.2 Inventory Service

- **FR-201**: Stock reservation for a given `productId` at a given `warehouseId` shall be protected by a **Redis distributed lock** to prevent overselling under concurrent order requests.
- **FR-202**: System shall support **multi-warehouse fallback** — if the nearest warehouse lacks stock, Inventory Service shall check the next-nearest warehouse before returning unavailability.
- **FR-203**: Reserved stock shall auto-release after a configurable TTL if the order is not confirmed within that window (handles abandoned checkouts).
- **FR-204**: Inventory Service shall consume `OrderCancelled` events and restore reserved stock, applying an **idempotency check via Redis** (keyed by event ID) to safely handle duplicate Kafka deliveries.
- **FR-205**: Vendors shall be able to bulk-update stock via a CSV upload endpoint, which internally emits per-SKU `StockAdjusted` events for audit and analytics.

### 4.3 Driver & Dispatch

- **FR-301**: Drivers shall report live GPS coordinates every 5–10 seconds; the latest position shall be stored in Redis (`driver:location:{driverId}`) with periodic snapshotting to Postgres for history.
- **FR-302**: The **Dispatch & ETA Agent Service** shall assign a driver to a confirmed order by scoring available drivers on: proximity (via Redis geo queries), current load, priority-order weighting, and historical on-time rate.
- **FR-401 (Agent behavior)**: If two or more candidate drivers score within a configurable margin of each other, the Dispatch Agent shall use LLM-based reasoning to break the tie using contextual factors (e.g., driver familiarity with the delivery zone, recent customer ratings) and log its reasoning trace for auditability.
- **FR-303**: If an assigned driver rejects or fails to acknowledge within a timeout, the system shall trigger automatic re-dispatch to the next-best candidate and emit a `DriverReassigned` event.
- **FR-304**: Dispatch Agent shall predict and continuously update an **ETA** per order based on live driver location, historical route data, and current traffic-condition input (simulated or via external API), publishing `ETAUpdated` events consumed by Tracking and Notification.
- **FR-305**: Drivers with 3+ SLA breaches (late deliveries) in a rolling 7-day window shall be automatically deprioritized in future dispatch scoring, with admin override capability.

### 4.4 Tracking Service

- **FR-501**: System shall expose a WebSocket channel per order (`/ws/track/{orderId}`) pushing real-time status and location updates to subscribed clients.
- **FR-502**: `GET /track/{orderId}` REST responses shall be cached in Redis, invalidated immediately on receipt of any `OrderStatusChanged` or `ETAUpdated` event for that order.
- **FR-503**: Tracking Service shall maintain a full immutable status history per order for customer-facing timeline display and dispute resolution.

### 4.5 AI Support Agent Service

- **FR-601**: System shall expose a chat endpoint where customers can ask natural-language questions about their orders (e.g., "Where is my package?", "Cancel order #123", "Why is my delivery late?").
- **FR-602**: The agent shall use **function/tool calling** to query live data from other services rather than hallucinating answers — available tools shall include: `getOrderStatus`, `getETA`, `cancelOrder`, `getRefundPolicy`, `escalateToHuman`.
- **FR-603**: The agent shall use **RAG (Retrieval-Augmented Generation)** over a knowledge base (FAQs, return policy, delivery SLAs) for policy-type questions, to avoid fabricating policy details.
- **FR-604**: All agent tool invocations shall be authorized against the requesting customer's identity — the agent must not be able to fetch or modify another customer's order data.
- **FR-605**: When the agent's confidence in resolving a query falls below a defined threshold, or the action requested is destructive (e.g., a refund above a configured amount), it shall invoke `escalateToHuman` and create a support ticket rather than acting autonomously.
- **FR-606**: Every agent conversation and tool call shall be logged with full reasoning trace for audit and later fine-tuning/evaluation.
- **FR-607**: The agent shall support multi-turn context — e.g., a customer asking "cancel it" after previously being told their order's ETA, without re-specifying the order ID.

### 4.6 Notification Service

- **FR-701**: System shall send notifications on: order confirmation, driver assignment, each status transition, ETA changes exceeding a delta threshold, and delivery completion.
- **FR-702**: Notification Service shall deduplicate deliveries using a Redis-backed idempotency key per `(eventId, channel)` pair.
- **FR-703**: Failed notification deliveries (e.g., SMS provider timeout) shall be retried with exponential backoff and routed to a **dead-letter topic** after exhausting retries, visible in an admin dashboard.

### 4.7 Analytics Service

- **FR-801**: System shall maintain read-model aggregates for: orders per day, average delivery time, SLA breach rate per driver/warehouse, and cancellation rate — built by consuming the same Kafka event streams as other services (no direct calls to Order/Driver DBs).
- **FR-802**: Admins shall be able to view a dashboard filterable by date range, warehouse, and driver.

### 4.8 Admin Ops Agent Service

- **FR-1001**: System shall expose an internal-only chat interface (accessible only to `ADMIN` role, never customer-facing) allowing natural-language CRUD operations and system debugging queries, e.g., "Show me all orders stuck in `ASSIGNED` for more than 2 hours" or "Deactivate driver #22."
- **FR-1002**: The agent shall operate exclusively through a fixed, admin-scoped toolset — it shall never execute raw SQL or access any data store directly. Minimum required tools:
  - `getOrderById`, `listOrdersByFilter` (status, age, warehouse, driver)
  - `cancelOrder`, `reassignDriver`, `updateStock`, `deactivateDriver`, `overrideOrderStatus`
  - `getDispatchTrace(orderId)` — retrieves the Dispatch Agent's event history and logged reasoning (per FR-401) for a given order
  - `getEventHistory(orderId)` — retrieves the full Kafka event trail for an order, for debugging
- **FR-1003**: Any tool call classified as **destructive or state-mutating** (`cancelOrder`, `reassignDriver`, `updateStock`, `deactivateDriver`, `overrideOrderStatus`) shall return a **proposed action with explanation** and shall NOT execute until the admin explicitly confirms it in the UI. Read-only tools (`getOrderById`, `getDispatchTrace`, etc.) may execute immediately.
- **FR-1004**: Every proposed and confirmed tool invocation shall be logged with `adminId`, timestamp, natural-language request, tool name, parameters, and outcome — forming a complete audit trail independent of the underlying service's own audit logs.
- **FR-1005**: The agent's tool layer shall enforce the same RBAC checks as the equivalent REST endpoints — the agent must never be able to perform an action the requesting admin's role would not otherwise be permitted to perform via the normal API.
- **FR-1006**: For debugging queries (e.g., "why did order #7788 fail to get a driver assigned"), the agent shall synthesize its answer strictly from retrieved tool outputs (event history, dispatch trace, stock logs) and shall not speculate beyond what the data shows; if the data is inconclusive, it shall state that explicitly rather than guessing.
- **FR-1007**: The agent shall support batch-style requests (e.g., "cancel all backordered orders older than 3 days") by first returning the full list of affected records for review, then requiring a single explicit confirmation before applying the batch action.
- **FR-1008**: All Admin Ops Agent sessions shall have a maximum permitted blast radius per confirmed batch action (configurable, e.g., max 50 records) to prevent a single mistaken confirmation from mutating the entire dataset.

### 4.9 API Gateway & Security

- **FR-901**: All external traffic shall route through the API Gateway; internal services shall not be publicly reachable.
- **FR-902**: Gateway shall enforce **role-based route access** (e.g., only `ADMIN` may access `/api/analytics/**`).
- **FR-903**: Gateway shall apply **Redis-backed rate limiting** per authenticated user and per IP for unauthenticated endpoints.
- **FR-904**: JWT tokens shall carry role and tenant claims validated at the Gateway before forwarding requests downstream.

---

## 5. Event Catalog (Kafka + Avro)

| Event | Producer | Consumers | Key Fields |
|---|---|---|---|
| `OrderCreated` | Order Service | Inventory, Notification, Analytics, Dispatch Agent | orderId, customerId, items[], warehouseId, priority |
| `OrderStatusChanged` | Order Service | Tracking, Notification, Analytics | orderId, oldStatus, newStatus, timestamp |
| `OrderCancelled` | Order Service | Inventory, Driver, Notification | orderId, reason |
| `StockAdjusted` | Inventory Service | Analytics | sku, warehouseId, delta |
| `DriverAssigned` | Dispatch Agent Service | Tracking, Notification, Driver | orderId, driverId, assignmentScore |
| `DriverReassigned` | Dispatch Agent Service | Tracking, Notification | orderId, oldDriverId, newDriverId, reason |
| `ETAUpdated` | Dispatch Agent Service | Tracking, Notification | orderId, newEta, deltaMinutes |
| `SLABreach` | Analytics/Dispatch | Notification, Analytics | driverId, orderId, delayMinutes |

**Schema evolution exercise**: launch `OrderCreated` without `priority`, then add it as an optional field with a default — verify existing consumers on the old schema version continue functioning under Schema Registry's `BACKWARD` compatibility mode.

---

## 6. Non-Functional Requirements

- **NFR-1**: Each service shall be independently deployable via its own Dockerfile; full stack shall run via a single `docker-compose.yml`.
- **NFR-2**: All inter-service async communication shall use Avro-serialized messages validated against Schema Registry.
- **NFR-3**: System shall implement distributed tracing (OpenTelemetry + Zipkin/Jaeger) propagating trace context across both REST and Kafka boundaries.
- **NFR-4**: AI Support Agent responses shall have a hard timeout (e.g., 10s) with graceful fallback messaging if the LLM provider is slow or unavailable.
- **NFR-5**: All destructive agent actions must be reversible or require confirmation.
- **NFR-6**: The Admin Ops Agent shall have no standing write access — every mutating tool call must pass through the confirm-before-execute gate (FR-1003) with no bypass path, even for repeated or "trusted" requests.

---

## 7. Build Phases

1. **Core REST + WebClient** — Customer, Order, Inventory (with Redis lock) — no Kafka yet.
2. **Event backbone** — Kafka + Avro + Schema Registry; Order publishes, Inventory/Notification consume; add Outbox pattern.
3. **Driver + Tracking + WebSockets** — live location via Redis, Tracking cache.
4. **Dispatch & ETA Agent** — rule-based scoring first, then layer in LLM tie-breaking reasoning.
5. **AI Support Agent** — tool-calling against existing REST endpoints, then add RAG knowledge base.
6. **Admin Ops Agent** — read-only debugging tools first (`getDispatchTrace`, `getEventHistory`), then layer in confirm-before-execute write tools.
7. **Gateway + Discovery + Config Server** — retrofit routing, Redis rate limiting, JWT enforcement.
8. **Analytics** — read models off existing event streams.
9. **Hardening** — Resilience4j, distributed tracing, chaos testing, Saga compensation paths.
