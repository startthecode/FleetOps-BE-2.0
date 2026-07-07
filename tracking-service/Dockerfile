# Build stage
FROM golang:1.25-alpine AS build
WORKDIR /app
COPY go.mod ./
RUN go mod download
COPY . .
RUN CGO_ENABLED=0 go build -o /tracking-service ./cmd/server

# Run stage
FROM alpine:3.21
COPY --from=build /tracking-service /tracking-service
EXPOSE 8085
ENTRYPOINT ["/tracking-service"]
