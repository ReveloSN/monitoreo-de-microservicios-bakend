# Sistema de Monitoreo con Proxy - Backend

Backend del taller universitario sobre el patrón Proxy. La aplicación simula tres microservicios internos (`inventory`, `orders`, `payments`) y coloca un `LoggingProxy` delante de cada uno para registrar auditoría, tiempos de respuesta, errores y métricas agregadas.

## Características principales

- Java 21 + Spring Boot 3
- API REST con respuestas JSON consistentes
- Patrón Proxy real mediante `MicroserviceProxy<T>`
- Logs filtrables con paginación
- Métricas agregadas por servicio
- Fallo aleatorio del 10% en pagos
- Datos demo en memoria y repositorios thread-safe
- Simulación de carga con 50 llamadas
- CORS listo para frontend local o despliegue
- Dockerfile listo para Railway o servicios similares

## Arquitectura

La aplicación está organizada por responsabilidades:

- `controller`: expone los endpoints REST
- `proxy`: concentra el patrón Proxy y los adaptadores por operación
- `service`: contiene la lógica real de inventario, órdenes y pagos
- `repository`: almacenamiento thread-safe en memoria
- `metrics`: cálculo de métricas y simulación de carga
- `dto`: contratos de entrada y salida
- `exception`: manejo global de errores
- `config`: CORS, proxies, datos demo y reloj UTC
- `util`: resolución de parámetros y utilidades técnicas

## Cómo se aplicó el patrón Proxy

Los controllers no llaman a los servicios reales directamente. Cada endpoint delega en un proxy:

1. El controller recibe la operación y sus parámetros.
2. El `LoggingProxy` genera un `requestId`, mide duración y captura contexto HTTP.
3. El proxy delega en un `ServiceOperationExecutor`.
4. Ese executor traduce el `operation` recibido hacia métodos reales del servicio correspondiente.
5. El proxy guarda el `AuditLogEntry` con éxito o error.

Esto deja separado el comportamiento transversal de observabilidad respecto a la lógica real del negocio.

## Endpoints

### Ejecución de servicios

- `POST /api/services/inventory/{operation}`
- `POST /api/services/orders/{operation}`
- `POST /api/services/payments/{operation}`

Body genérico:

```json
{
  "params": {
    "key": "value"
  }
}
```

Operaciones disponibles:

- `inventory`: `listItems`, `getItemById`, `reserveStock`
- `orders`: `createOrder`, `getOrderById`, `listOrders`
- `payments`: `processPayment`, `getPaymentById`

### Métricas y observabilidad

- `GET /api/metrics/summary`
- `GET /api/metrics/logs?service=&status=&from=&to=&page=&size=`
- `POST /api/metrics/simulate-load`

## Requisitos previos

- Java 21
- Maven 3.9+

## Ejecución local

Si `mvn` ya está en el `PATH`:

```bash
mvn spring-boot:run
```

Si necesitas compilar y ejecutar pruebas:

```bash
mvn test
mvn clean package
```

La aplicación queda disponible en:

```text
http://localhost:8080
```

## Ejecución con Docker

```bash
docker build -t proxy-monitoring-backend .
docker run -p 8080:8080 proxy-monitoring-backend
```

## Ejemplos rápidos con curl

Listar inventario:

```bash
curl -X POST http://localhost:8080/api/services/inventory/listItems \
  -H "Content-Type: application/json" \
  -d "{\"params\":{}}"
```

Consultar un item:

```bash
curl -X POST http://localhost:8080/api/services/inventory/getItemById \
  -H "Content-Type: application/json" \
  -d "{\"params\":{\"itemId\":1}}"
```

Reservar stock:

```bash
curl -X POST http://localhost:8080/api/services/inventory/reserveStock \
  -H "Content-Type: application/json" \
  -d "{\"params\":{\"itemId\":3,\"quantity\":2}}"
```

Crear una orden:

```bash
curl -X POST http://localhost:8080/api/services/orders/createOrder \
  -H "Content-Type: application/json" \
  -d "{\"params\":{\"customerName\":\"María Gómez\",\"itemId\":4,\"quantity\":1}}"
```

Listar órdenes:

```bash
curl -X POST http://localhost:8080/api/services/orders/listOrders \
  -H "Content-Type: application/json" \
  -d "{\"params\":{}}"
```

Procesar un pago:

```bash
curl -X POST http://localhost:8080/api/services/payments/processPayment \
  -H "Content-Type: application/json" \
  -d "{\"params\":{\"orderId\":1,\"amount\":2400,\"paymentMethod\":\"CARD\"}}"
```

Consultar métricas:

```bash
curl http://localhost:8080/api/metrics/summary
```

Consultar logs paginados:

```bash
curl "http://localhost:8080/api/metrics/logs?page=0&size=10&service=payments&status=ERROR"
```

Simular carga:

```bash
curl -X POST http://localhost:8080/api/metrics/simulate-load
```

## Datos demo

Al iniciar la aplicación se cargan automáticamente:

- 4 productos de inventario
- 2 órdenes
- 2 pagos exitosos

## Tests incluidos

- `LoggingProxyTest`: éxito y error
- `PaymentServiceTest`: fallo simulado y persistencia
- `MetricsServiceTest`: agregación de métricas
- `ServiceExecutionControllerIntegrationTest`: integración REST con MockMvc
