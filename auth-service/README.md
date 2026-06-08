# Auth Service — Authentication & Authorization

Handles user registration, authentication, and role management. Issues JWT tokens for API access.

## Tech Stack

- Java 21
- Spring Boot 3.2.0
- Spring Cloud 2023.0.0 (Eureka Client)
- Spring Security
- Spring Data JPA
- PostgreSQL
- JJWT 0.12.3 (JWT creation/validation)
- MapStruct 1.5.5.Final (DTO mapping)
- Springdoc OpenAPI 2.3.0 (Swagger docs)
- Spring Boot Actuator

## Configuration

| Property | Value |
|---|---|
| Port | `8081` |
| DB Schema | `auth` |
| Swagger UI | `http://localhost:8081/swagger-ui/index.html` |

## Environment Variables

| Variable | Description | Default |
|---|---|---|
| `SPRING_DATASOURCE_URL` | PostgreSQL JDBC URL | `jdbc:postgresql://localhost:5432/furniture_db` |
| `EUREKA_CLIENT_SERVICEURL_DEFAULTZONE` | Eureka server URL | `http://localhost:8761/eureka` |
| `JWT_SECRET` | Secret key for signing JWT tokens | `change-me-in-production` |
| `JWT_EXPIRATION` | Token expiration in milliseconds | `86400000` (24h) |

## API Endpoints

### Authentication

| Method | Path | Auth | Description |
|---|---|---|---|
| POST | `/api/v1/auth/login` | Public | Authenticate and get JWT token |
| POST | `/api/v1/auth/register` | ADMIN | Create new user |

### User Management

| Method | Path | Auth | Description |
|---|---|---|---|
| GET | `/api/v1/auth/users` | ADMIN | List users (paginated) |
| GET | `/api/v1/auth/users/{id}` | ADMIN | Get user by ID |
| PATCH | `/api/v1/auth/users/{id}/status` | ADMIN | Enable/disable user |
| PATCH | `/api/v1/auth/users/{id}/password` | ADMIN | Change user password |

### Roles

| Method | Path | Auth | Description |
|---|---|---|---|
| GET | `/api/v1/auth/roles` | ADMIN | List all roles |
| POST | `/api/v1/auth/roles` | ADMIN | Create new role |

## How to Run

### With Docker

```bash
docker compose up --build auth-service
```

### Locally with Maven

```bash
cd auth-service
mvn spring-boot:run
```

## Dependencies

- **eureka-server** (must be healthy before starting)
- **PostgreSQL** (with `auth` schema)

## Swagger

Access interactive API documentation at:

```
http://localhost:8081/swagger-ui/index.html
```
