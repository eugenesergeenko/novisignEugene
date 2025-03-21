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

## Getting Started

### Prerequisites

- Java 17+
- Docker & Docker Compose
- Maven or Gradle

---

### Run with Docker

```bash
# Start app with database
docker-compose up --build
