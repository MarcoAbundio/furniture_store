# API Gateway — Entry Point

Spring Cloud Gateway that serves as the single entry point for the Furniture Store microservices. Handles JWT validation, request routing, and load balancing via Eureka.

## Tech Stack

- Java 21
- Spring Boot 3.2.0
- Spring Cloud 2023.0.0 (Gateway, LoadBalancer, Eureka Client)
- JJWT 0.12.3 (JWT token validation)
- Spring Boot Actuator

## Configuration

| Property | Value |
|---|---|
| Port | `8080` |

## Environment Variables

| Variable | Description | Default |
|---|---|---|
| `EUREKA_CLIENT_SERVICEURL_DEFAULTZONE` | Eureka server URL | `http://localhost:8761/eureka` |
| `JWT_SECRET` | Secret key for JWT validation | `change-me-in-production` |

## Routes

| Path | Target Service | Auth Required |
|---|---|---|
| `/api/v1/auth/**` | auth-service | Public (login), JWT (others) |
| `/api/v1/products/**` | product-service | JWT |
| `/api/v1/categories/**` | product-service | JWT |
| `/api/v1/stock/**` | product-service | JWT |
| `/api/v1/employees/**` | employee-service | JWT |
| `/api/v1/departments/**` | employee-service | JWT |
| `/api/v1/positions/**` | employee-service | JWT |
| `/api/v1/deliveries/**` | delivery-service | JWT |
| `/api/v1/customers/**` | delivery-service | JWT |

## Public Paths (No JWT Required)

- `/api/v1/auth/login`
- `/actuator/health`
- Any path containing `/swagger-ui/`
- Any path ending with `/v3/api-docs`

## How to Run

### With Docker

```bash
docker compose up --build api-gateway
```

### Locally with Maven

```bash
cd api-gateway
mvn spring-boot:run
```

## Dependencies

- **eureka-server** (must be healthy before starting)

## Notes

- All requests pass through a `GlobalFilter` that validates the JWT token
- Validated user info is forwarded to downstream services via `X-User-*` headers
- Routes are defined in `application.yml` under `spring.cloud.gateway.routes`
