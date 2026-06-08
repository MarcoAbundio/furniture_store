# Delivery Service — Logistics & Deliveries

Manages customer registrations, delivery addresses, and delivery orders with full status tracking.

## Tech Stack

- Java 21
- Spring Boot 3.2.0
- Spring Cloud 2023.0.0 (Eureka Client)
- Spring Security
- Spring Data JPA
- PostgreSQL
- JJWT 0.12.3 (JWT validation)
- MapStruct 1.5.5.Final (DTO mapping)
- Springdoc OpenAPI 2.3.0 (Swagger docs)
- Spring Boot Actuator

## Configuration

| Property | Value |
|---|---|
| Port | `8084` |
| DB Schema | `logistics` |
| Swagger UI | `http://localhost:8084/swagger-ui/index.html` |

## Environment Variables

| Variable | Description | Default |
|---|---|---|
| `SPRING_DATASOURCE_URL` | PostgreSQL JDBC URL | `jdbc:postgresql://localhost:5432/furniture_db` |
| `EUREKA_CLIENT_SERVICEURL_DEFAULTZONE` | Eureka server URL | `http://localhost:8761/eureka` |
| `JWT_SECRET` | Secret key for JWT validation | `change-me-in-production` |

## API Endpoints

### Customers

| Method | Path | Auth | Description |
|---|---|---|---|
| POST | `/api/v1/customers` | ADMIN, CASHIER | Register customer |
| GET | `/api/v1/customers` | ADMIN, CASHIER | List customers (paginated) |
| GET | `/api/v1/customers/{id}` | ADMIN, CASHIER | Get customer by ID |
| GET | `/api/v1/customers/search?q=` | ADMIN, CASHIER | Search customers |
| PUT | `/api/v1/customers/{id}` | ADMIN, CASHIER | Update customer |
| DELETE | `/api/v1/customers/{id}` | ADMIN | Delete customer |

### Addresses

| Method | Path | Auth | Description |
|---|---|---|---|
| POST | `/api/v1/customers/addresses` | ADMIN, CASHIER | Add delivery address |
| GET | `/api/v1/customers/{id}/addresses` | ADMIN, CASHIER | List customer addresses |
| PATCH | `/api/v1/customers/addresses/{addressId}/set-default` | ADMIN, CASHIER | Set default address |

### Deliveries

| Method | Path | Auth | Description |
|---|---|---|---|
| POST | `/api/v1/deliveries` | ADMIN, CASHIER | Create delivery order |
| GET | `/api/v1/deliveries` | ADMIN, CASHIER | List deliveries (paginated) |
| GET | `/api/v1/deliveries/{id}` | ADMIN, CASHIER | Get delivery by ID |
| GET | `/api/v1/deliveries/number/{deliveryNumber}` | ADMIN, CASHIER | Get by delivery number |
| GET | `/api/v1/deliveries/customer/{customerId}` | ADMIN, CASHIER | Deliveries by customer |
| GET | `/api/v1/deliveries/employee/{employeeId}` | ADMIN, CASHIER | Deliveries by employee |
| GET | `/api/v1/deliveries/status/{status}` | ADMIN, CASHIER | Deliveries by status |
| GET | `/api/v1/deliveries/date-range` | ADMIN, CASHIER | Deliveries by date range |
| PATCH | `/api/v1/deliveries/{id}/status` | ADMIN, CASHIER | Update delivery status |
| DELETE | `/api/v1/deliveries/{id}` | ADMIN | Cancel delivery |

### Delivery Status Flow

```
PENDING → SCHEDULED → IN_TRANSIT → DELIVERED
                    ↘             ↗
                     CANCELLED / FAILED
```

## How to Run

### With Docker

```bash
docker compose up --build delivery-service
```

### Locally with Maven

```bash
cd delivery-service
mvn spring-boot:run
```

## Dependencies

- **eureka-server** (must be healthy before starting)
- **PostgreSQL** (with `logistics` schema)

## Swagger

Access interactive API documentation at:

```
http://localhost:8084/swagger-ui/index.html
```
