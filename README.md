# 🛋️ Furniture Store — Microservices REST API

Java 21 + Spring Boot 3.2 + PostgreSQL · Full microservices architecture

---

## 📐 Architecture

```
                          ┌─────────────────────────────────────────────────────┐
  Client (Postman/UI)     │              API GATEWAY  :8080                     │
  ───────────────────►    │   JWT Validation · Load Balancing · Routing         │
                          └────────────────┬────────────────────────────────────┘
                                           │ Eureka Service Discovery :8761
                         ┌─────────────────┼──────────────────────┐
                         ▼                 ▼                       ▼           ▼
                  auth-service      product-service       employee-service  delivery-service
                    :8081              :8082                  :8083             :8084
                         │                 │                       │              │
                         └─────────────────┴───────────────────────┴──────────────┘
                                                        │
                                                 PostgreSQL :5432
                                          (schemas: auth, inventory, hr, logistics)
```

## 🗂️ Microservices

| Service           | Port | Description                              |
|-------------------|------|------------------------------------------|
| eureka-server     | 8761 | Service registry (Netflix Eureka)        |
| api-gateway       | 8080 | Entry point · JWT filter · Route proxy   |
| auth-service      | 8081 | Users · Roles · JWT authentication       |
| product-service   | 8082 | Products · Categories · Stock            |
| employee-service  | 8083 | Employees · Departments · Positions      |
| delivery-service  | 8084 | Customers · Addresses · Deliveries       |

## 🔐 Security — Role Matrix

| Endpoint group             | ROLE_ADMIN | ROLE_CASHIER |
|----------------------------|:----------:|:------------:|
| POST /auth/register        | ✅         | ❌            |
| GET/PATCH /auth/users      | ✅         | ❌            |
| CRUD /products             | ✅         | GET only      |
| CRUD /categories           | ✅         | GET only      |
| PATCH /stock/adjust        | ✅         | ❌            |
| CRUD /employees            | ✅         | GET only      |
| CRUD /departments          | ✅         | GET only      |
| POST/GET /customers        | ✅         | ✅            |
| POST/GET/PATCH /deliveries | ✅         | ✅            |
| DELETE /deliveries         | ✅         | ❌            |

## 🔐 Environment Setup

Before running, copy the example environment file and fill in your values:

```bash
cp .env.example .env
```

Required variables in `.env`:

| Variable | Description | Example |
|---|---|---|
| `SPRING_DATASOURCE_URL` | PostgreSQL JDBC URL (Neon or local) | `jdbc:postgresql://host:5432/db?user=u&password=p&sslmode=require` |
| `JWT_SECRET` | Secret key for JWT signing/validation | `your-256-bit-secret` |
| `JWT_EXPIRATION` | Token lifetime in milliseconds | `86400000` |

## 🚀 Quick Start

### Prerequisites
- Docker & Docker Compose
- Java 21 + Maven (for local development)

### Run everything with Docker

```bash
git clone <repo>
cd furniture-store
docker compose up --build
```

All 6 services and the database start in dependency order:
Eureka → Auth/Product/Employee/Delivery → Gateway

### First login (after DB seeds)

```bash
# The init-db.sql creates roles only (ROLE_ADMIN, ROLE_CASHIER).
# Create your first admin user via the API:
curl -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin1234"}'
```

> If the admin user does not exist yet, register one:
> ```bash
> curl -X POST http://localhost:8080/api/v1/auth/register \
>   -H "Content-Type: application/json" \
>   -H "Authorization: Bearer <admin-token>" \
>   -d '{"username":"admin","password":"admin1234","email":"admin@furniturestore.com","roleId":1}'
> ```

Use the returned `token` as `Authorization: Bearer <token>` on all subsequent requests.

## 📖 Service Documentation

Each microservice has its own README with full API endpoint documentation:

| Service | README |
|---|---|
| Eureka Server | [eureka-server/README.md](eureka-server/README.md) |
| API Gateway | [api-gateway/README.md](api-gateway/README.md) |
| Auth Service | [auth-service/README.md](auth-service/README.md) |
| Product Service | [product-service/README.md](product-service/README.md) |
| Employee Service | [employee-service/README.md](employee-service/README.md) |
| Delivery Service | [delivery-service/README.md](delivery-service/README.md) |

### Swagger UI (per service)

| Service          | URL |
|---|---|
| Auth Service | `http://localhost:8081/swagger-ui/index.html` |
| Product Service | `http://localhost:8082/swagger-ui/index.html` |
| Employee Service | `http://localhost:8083/swagger-ui/index.html` |
| Delivery Service | `http://localhost:8084/swagger-ui/index.html` |
| Eureka Dashboard | `http://localhost:8761` |

## 📦 Key API Endpoints

### Authentication
```
POST   /api/v1/auth/login              → Get JWT token
POST   /api/v1/auth/register           → Create user [ADMIN]
GET    /api/v1/auth/users?page=0&size=10
PATCH  /api/v1/auth/users/{id}/status  → Enable/disable user [ADMIN]
PATCH  /api/v1/auth/users/{id}/password
```

### Products & Inventory
```
POST   /api/v1/products               → Create product [ADMIN]
GET    /api/v1/products?page=0&size=10
GET    /api/v1/products/{id}
GET    /api/v1/products/sku/{sku}
GET    /api/v1/products/search?q=sofa
GET    /api/v1/products/category/{categoryId}
PUT    /api/v1/products/{id}          → [ADMIN]
DELETE /api/v1/products/{id}          → Soft-delete [ADMIN]

GET    /api/v1/stock/product/{productId}
PATCH  /api/v1/stock/product/{productId}/adjust  → INCREMENT/DECREMENT [ADMIN]
GET    /api/v1/stock/low-stock        → [ADMIN]
```

### Home Deliveries (Coppel-style)
```
POST   /api/v1/customers              → Register customer
GET    /api/v1/customers?page=0&size=10
GET    /api/v1/customers/search?q=maria
POST   /api/v1/customers/addresses    → Add delivery address
GET    /api/v1/customers/{id}/addresses
PATCH  /api/v1/customers/addresses/{addressId}/set-default

POST   /api/v1/deliveries             → Create delivery order
GET    /api/v1/deliveries?page=0&size=10
GET    /api/v1/deliveries/{id}
GET    /api/v1/deliveries/number/{deliveryNumber}
GET    /api/v1/deliveries/customer/{customerId}
GET    /api/v1/deliveries/employee/{employeeId}
GET    /api/v1/deliveries/status/PENDING
GET    /api/v1/deliveries/date-range?from=2024-06-01&to=2024-06-30
PATCH  /api/v1/deliveries/{id}/status → Update status
DELETE /api/v1/deliveries/{id}        → Cancel [ADMIN]
```

### Delivery Status Flow
```
PENDING → SCHEDULED → IN_TRANSIT → DELIVERED
                    ↘             ↗
                     CANCELLED / FAILED
```

## 🗄️ Database Schema (Normalized — 3NF+)

```
auth schema:
  roles ──< users

inventory schema:
  categories (self-referencing parent/child) ──< products ──── stock (1:1)

hr schema:
  departments ──< positions ──< employees

logistics schema:
  customers ──< addresses
  customers ──< deliveries
  addresses ──< deliveries
  employees (by id) ──< deliveries
  deliveries ──< delivery_items >── products (by id)
  delivery_status_catalog ──< deliveries
```

## 🏗️ Each Service Structure

```
src/main/java/com/furniturestore/<service>/
├── config/          # SecurityConfig, SwaggerConfig
├── controller/      # REST controllers
├── dto/
│   ├── request/     # *Request.java  (input validation)
│   └── response/    # *Response.java (output projection)
├── exception/       # GlobalExceptionHandler, custom exceptions
├── mapper/          # MapStruct mappers (no recursion risk)
├── model/           # JPA entities (all LAZY fetches)
├── repository/      # Spring Data JPA with custom JPQL
├── security/        # JwtTokenProvider, JwtAuthenticationFilter
└── service/         # *Service interface + *ServiceImpl
```

## 🧪 Testing

Each service has Mockito unit tests under `src/test/`:
```bash
cd auth-service && mvn test
cd product-service && mvn test
cd employee-service && mvn test
cd delivery-service && mvn test
```

## ✅ Design Principles Applied

- **SOLID**: Single responsibility per class, interfaces for services, DI everywhere
- **Clean Architecture**: Controller → Service → Repository; DTOs isolate layers
- **No infinite recursion**: All entity relationships use `FetchType.LAZY`; DTOs are flat projections via MapStruct
- **Normalized DB**: 4 schemas, foreign keys, no data duplication
- **Pagination**: All list endpoints return `Page<T>` with configurable page/size/sort
- **Soft deletes**: Products and employees use `isActive=false` instead of hard delete
- **Transactional safety**: `@Transactional(readOnly=true)` for reads, `@Transactional` for writes
- **Security**: JWT validated at gateway + re-validated per service; role-based `@PreAuthorize`
- **Swagger**: OpenAPI 3 docs on every service with Bearer auth scheme
