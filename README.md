# ConsentFlow - Consent Management API

An enterprise-grade backend API for managing user permissions and authorization records in a fictional document-sharing service. Built with Domain-Driven Design (DDD) principles.

## 🚀 Tech Stack
- **Language:** Java 17
- **Framework:** Spring Boot 3.2.5
- **Database:** PostgreSQL (with Flyway Migrations)
- **Testing:** JUnit 5, MockMvc, Testcontainers
- **Documentation:** Swagger / OpenAPI 3.0
- **Containerization:** Docker, Docker Compose

## 🏗️ Architecture

```mermaid
graph TD
    Client[Client / Frontend] -->|HTTP / X-User-Id| API[Spring Boot API Gateway]
    
    subgraph "Spring Boot Application"
        API --> Controller[ConsentController]
        Controller --> Service[ConsentService<br/>State Machine & Ownership]
        Service --> Repo[Repositories]
        Controller -.-> Exception[Global Exception Handler]
        
        Repo --> DB[(PostgreSQL)]
    end
    
    style API fill:#f9f,stroke:#333,stroke-width:2px