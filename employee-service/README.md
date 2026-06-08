# Employee Service — HR Management

Manages employees, departments, and positions for the Furniture Store workforce.

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
| Port | `8083` |
| DB Schema | `hr` |
| Swagger UI | `http://localhost:8083/swagger-ui/index.html` |

## Environment Variables

| Variable | Description | Default |
|---|---|---|
| `SPRING_DATASOURCE_URL` | PostgreSQL JDBC URL | `jdbc:postgresql://localhost:5432/furniture_db` |
| `EUREKA_CLIENT_SERVICEURL_DEFAULTZONE` | Eureka server URL | `http://localhost:8761/eureka` |
| `JWT_SECRET` | Secret key for JWT validation | `change-me-in-production` |

## API Endpoints

### Employees

| Method | Path | Auth | Description |
|---|---|---|---|
| GET | `/api/v1/employees` | ADMIN, CASHIER | List employees (paginated) |
| GET | `/api/v1/employees/{id}` | ADMIN, CASHIER | Get employee by ID |
| GET | `/api/v1/employees/code/{employeeCode}` | ADMIN, CASHIER | Get by employee code |
| GET | `/api/v1/employees/search?q=` | ADMIN, CASHIER | Search employees |
| GET | `/api/v1/employees/department/{departmentId}` | ADMIN, CASHIER | Employees by department |
| GET | `/api/v1/employees/position/{positionId}` | ADMIN, CASHIER | Employees by position |
| POST | `/api/v1/employees` | ADMIN | Create employee |
| PUT | `/api/v1/employees/{id}` | ADMIN | Update employee |
| DELETE | `/api/v1/employees/{id}` | ADMIN | Soft-delete employee |

### Departments

| Method | Path | Auth | Description |
|---|---|---|---|
| GET | `/api/v1/departments` | ADMIN, CASHIER | List departments |
| GET | `/api/v1/departments/{id}` | ADMIN, CASHIER | Get department by ID |
| POST | `/api/v1/departments` | ADMIN | Create department |
| PUT | `/api/v1/departments/{id}` | ADMIN | Update department |

### Positions

| Method | Path | Auth | Description |
|---|---|---|---|
| GET | `/api/v1/positions` | ADMIN, CASHIER | List positions |
| GET | `/api/v1/positions/{id}` | ADMIN, CASHIER | Get position by ID |
| GET | `/api/v1/positions/department/{departmentId}` | ADMIN, CASHIER | Positions by department |
| POST | `/api/v1/positions` | ADMIN | Create position |
| PUT | `/api/v1/positions/{id}` | ADMIN | Update position |

## How to Run

### With Docker

```bash
docker compose up --build employee-service
```

### Locally with Maven

```bash
cd employee-service
mvn spring-boot:run
```

## Dependencies

- **eureka-server** (must be healthy before starting)
- **PostgreSQL** (with `hr` schema)

## Swagger

Access interactive API documentation at:

```
http://localhost:8083/swagger-ui/index.html
```
