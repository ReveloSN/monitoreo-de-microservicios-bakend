# Proxy Monitoring Backend Design

**Date:** 2026-04-17

## Goal

Build a single Spring Boot backend that simulates three monitored microservices (inventory, orders, payments) behind a reusable logging proxy, exposing audit logs, aggregated metrics, and load simulation endpoints for a future observability dashboard.

## Architecture

- The application will be organized around clear boundaries: controllers, DTOs, services, repositories, metrics, proxy, config, exceptions, and utilities.
- Real domain behavior will live in service implementations.
- Controllers will call proxies only.
- Each proxy will wrap an operation executor that translates an operation string plus generic request parameters into real service method calls.
- Audit logs and demo data will be stored in thread-safe in-memory repositories, behind interfaces that can later be reimplemented with PostgreSQL without changing the HTTP contracts.

## Core Design Choices

- Use `MicroserviceProxy<T>` as the public proxy contract.
- Use a generic `LoggingProxy` to add auditing, timing, request IDs, and error capture around any service operation executor.
- Use immutable DTOs and focused model classes.
- Use UTC and ISO-8601 timestamps in all responses and logs.
- Keep user-visible messages in Spanish and code identifiers in English.
- Simulate payment failures through an injectable probabilistic component to keep the 10% failure rule testable.

## Data Flow

1. The controller receives a generic request body with `params`.
2. The controller selects the service proxy based on the endpoint.
3. The proxy generates a request ID, captures request context, delegates execution, and writes a success or error audit record.
4. The response is wrapped in a consistent API envelope.
5. Metrics endpoints query the audit repository to compute summary cards, service breakdowns, recent calls, and paginated log views.

## Error Handling

- Unsupported operations return a structured 400 response.
- Missing entities return 404.
- Simulated payment failures return a controlled error response in Spanish.
- Validation and unexpected failures are normalized through `@RestControllerAdvice`.

## Testing Strategy

- Unit tests for `LoggingProxy` success and error flows.
- Unit tests for payment failure simulation behavior.
- Unit tests for metrics aggregation.
- Integration test for controller execution via MockMvc.

