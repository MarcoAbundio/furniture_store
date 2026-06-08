# Product Service — Catalog & Inventory

Manages the product catalog, categories, and stock levels for the Furniture Store.

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
| Port | `8082` |
| DB Schema | `inventory` |
| Swagger UI | `http://localhost:8082/swagger-ui/index.html` |

## Environment Variables

| Variable | Description | Default |
|---|---|---|
| `SPRING_DATASOURCE_URL` | PostgreSQL JDBC URL | `jdbc:postgresql://localhost:5432/furniture_db` |
| `EUREKA_CLIENT_SERVICEURL_DEFAULTZONE` | Eureka server URL | `http://localhost:8761/eureka` |
| `JWT_SECRET` | Secret key for JWT validation | `change-me-in-production` |

## API Endpoints

### Products

| Method | Path | Auth | Description |
|---|---|---|---|
| GET | `/api/v1/products` | ADMIN, CASHIER | List products (paginated) |
| GET | `/api/v1/products/{id}` | ADMIN, CASHIER | Get product by ID |
| GET | `/api/v1/products/sku/{sku}` | ADMIN, CASHIER | Get product by SKU |
| GET | `/api/v1/products/search?q=` | ADMIN, CASHIER | Search products |
| GET | `/api/v1/products/category/{categoryId}` | ADMIN, CASHIER | Products by category |
| POST | `/api/v1/products` | ADMIN | Create product |
| PUT | `/api/v1/products/{id}` | ADMIN | Update product |
| DELETE | `/api/v1/products/{id}` | ADMIN | Soft-delete product |

### Categories

| Method | Path | Auth | Description |
|---|---|---|---|
| GET | `/api/v1/categories` | ADMIN, CASHIER | List categories |
| GET | `/api/v1/categories/{id}` | ADMIN, CASHIER | Get category by ID |
| GET | `/api/v1/categories/{id}/children` | ADMIN, CASHIER | Get subcategories |
| POST | `/api/v1/categories` | ADMIN | Create category |
| PUT | `/api/v1/categories/{id}` | ADMIN | Update category |

### Stock

| Method | Path | Auth | Description |
|---|---|---|---|
| GET | `/api/v1/stock/product/{productId}` | ADMIN, CASHIER | Get stock for product |
| PATCH | `/api/v1/stock/product/{productId}/adjust` | ADMIN | Adjust stock (INCREMENT/DECREMENT) |
| GET | `/api/v1/stock/low-stock` | ADMIN | List products below minimum stock |

## How to Run

### With Docker

```bash
docker compose up --build product-service
```

### Locally with Maven

```bash
cd product-service
mvn spring-boot:run
```

## Dependencies

- **eureka-server** (must be healthy before starting)
- **PostgreSQL** (with `inventory` schema)

## Swagger

Access interactive API documentation at:

```
http://localhost:8082/swagger-ui/index.html
```
