# Eugene's Slideshow API

A home assignment task to manage image URLs and playing slideshow content with proof-of-play event logging. Built with reactive programming using WebFlux, event-driven architecture, and containerized for easy deployment.

---

## Features

- Add, retrieve, update, and delete image URLs
- Slideshow playback with transitions
- Proof-of-play event logging
- Reactive API using **Spring WebFlux**
- Event-driven actions via **Spring EventPublisher**
- MySQL
- Standardized error handling
- JUnit 5 test suite
- Dockerized with `Dockerfile` and `docker-compose.yml`

---

## Tech Stack

- Java 17+
- Spring Boot
- Spring WebFlux
- R2DBC (Reactive DB Access)
- MySQL
- Docker, Docker Compose
- JUnit 5
- H2 (for integration tests)

---

##  Getting Started

###  Prerequisites

- Java 17+
- Docker & Docker Compose
- Maven or Gradle

---

### Run with Docker

```bash
# Start app with database
docker-compose up --build
```

Access API at: `http://localhost:8080`

---

###  Run Tests

```bash
./mvnw test
```

Integration tests use **H2** database in-memory for isolation.

---

## API Endpoints

| Method | Endpoint                                   | Description                         |
|--------|--------------------------------------------|-------------------------------------|
| GET    | `/images/search`                           | Search images by given URL fragment |
| POST   | `/images/add`                              | Add a new image URL                 |
| DELETE | `/images/{id}`                             | Delete image by ID                  |
| POST   | `/slideshow/add`                           | Add new Slideshow                   |
| DELETE | `/slideshow//delete/{id}`                  | Delete slideshow by ID              |
| GET    | `/slideshow/{id}/images`                   | Get images in a slideshow           |
| POST   | `/slideshow/{id}/proof-of-play/{imageId}`  | Register a proof-of-play event      |

 All API actions like adding or deleting an image will trigger internal **Spring events** for logging or side-effects (e.g., proof-of-play).

---

## 🧬 Project Structure

```
src
├── main
│   ├── java/com/novi/eugene
│   │   ├── config
│   │   ├── controllers
│   │   ├── data
│   │   ├── dto
│   │   ├── events
│   │   ├── exceptions
│   │   ├── services
│   │   └── Application.java
│   └── resources
│       ├── application.yml
└── test
    └── java/com/novi/eugene
        ├── controllers
        └── integration
```

---

## Configuration

**`application.yml`** example:

```yaml
server:
  port: 8080

spring:
  application:
    name: slideshow-app

  r2dbc:
    url: ${SPRING_R2DBC_URL:r2dbc:mysql://localhost:3306/novi}
    username: ${SPRING_R2DBC_USERNAME:root}
    password: ${SPRING_R2DBC_PASSWORD:password}
    pool:
      enabled: true

  flyway:
    enabled: false

  main:
    web-application-type: reactive

  # ✅ Swagger Configuration (Springdoc OpenAPI)
  springdoc:
    api-docs:
      enabled: true
      path: /v3/api-docs  # OpenAPI JSON URL: http://localhost:8080/v3/api-docs
    swagger-ui:
      enabled: true
      path: /swagger-ui.html  # Swagger UI URL: http://localhost:8080/swagger-ui.html
      operations-sorter: method  # Sort endpoints by method (GET, POST, etc.)
      tags-sorter: alpha  # Sort API tags alphabetically
    show-actuator: true  # Show Actuator endpoints in Swagger

  # ✅ CORS Configuration (Allow Frontend Requests)
  web:
    cors:
      allowed-origins: "http://localhost:3000"
      allowed-methods: "*"
      allowed-headers: "*"
      allow-credentials: true

logging:
  level:
    root: INFO
    org.springframework.web: DEBUG  # Debug HTTP requests
    org.springframework.r2dbc: DEBUG  # Debug database queries
    org.testcontainers: INFO  # Testcontainers logging

management:
  endpoints:
    web:
      exposure:
        include: health, info, metrics
  endpoint:
    health:
      show-details: always
```

---

## Validation

- Image URL is validated to ensure it's a valid image format (e.g., `.png`, `.jpg`)
- Standardized error responses with error codes and messages

---

## Developer Notes

- Integration tests use H2 for fast, isolated testing
- All endpoints are non-blocking using WebFlux
- Events use `NoviEventPublisher` for loose coupling
- Sample payloads for testing provided in swagger at http://localhost:8080/swagger-ui.html
---