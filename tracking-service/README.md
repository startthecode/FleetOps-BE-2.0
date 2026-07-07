# Tracking Service (Go)

Placeholder skeleton for the FleetOps Tracking Service, written in Go.

## Planned responsibilities (per FRD §4.4)

- Consume `OrderStatusChanged` and `ETAUpdated` Kafka events
- Push real-time status/location updates over WebSocket (`/ws/track/{orderId}`)
- Serve `GET /track/{orderId}` with Redis-backed caching
- Maintain immutable per-order status history

## Layout

```
tracking-service/
├── cmd/server/       # entry point
├── internal/         # private application code (handlers, kafka, redis, store)
├── go.mod
└── Dockerfile
```

## Run

```sh
go run ./cmd/server
# health check
curl http://localhost:8085/health
```
