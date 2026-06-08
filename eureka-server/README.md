# Eureka Server — Service Registry

Service registry for the Furniture Store microservices ecosystem, based on Netflix Eureka.

## Tech Stack

- Java 21
- Spring Boot 3.2.0
- Spring Cloud 2023.0.0 (Netflix Eureka Server)
- Spring Boot Actuator

## Configuration

| Property | Value |
|---|---|
| Port | `8761` |
| Health endpoint | `/actuator/health` |
| Dashboard | `http://localhost:8761` |

## Environment Variables

None required. The server runs standalone.

## How to Run

### With Docker

```bash
docker compose up --build eureka-server
```

### Locally with Maven

```bash
cd eureka-server
mvn spring-boot:run
```

## Eureka Dashboard

Once running, access the Eureka Dashboard at:

```
http://localhost:8761
```

All registered services appear under "Instances currently registered with Eureka".

## Dependencies

- Standalone service (no dependencies on other services)
