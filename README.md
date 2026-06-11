# Medical Appointment System - Backend API

This repository contains the backend core logic for a Medical Appointment Management System built on the Spring Boot framework. The application handles core functionalities including role-based access control, appointment scheduling, and automated email notifications, operating on a containerized infrastructure.

## Project Architecture & Tech Stack

The system strictly adheres to the standard Controller-Service-Repository pattern to ensure separation of concerns.

* **Core Framework:** Java 21, Spring Boot 4.0.3
* **Database & Persistence:** PostgreSQL 16, Spring Data JPA, Hibernate
* **Security:** Spring Security, JSON Web Token (JWT)
* **Infrastructure:** Docker, Docker Compose (Multi-stage build)

### Key Implementation Details

* **Database Reliability:** Implemented Soft Deletes via Hibernate `@SQLDelete` and Optimistic Locking with `@Version` to handle concurrent data modifications safely.
* **Global Exception Handling:** Centralized error handling using Spring's `@ControllerAdvice` to ensure unified, predictable, and clean standard API responses (`ApiResponse`) across all endpoints.
* **Advanced Data Validation:** Beyond standard Jakarta validations, implemented custom constraint annotations (e.g., `@NotPastDate`, `@ValidTimeRange`) to strictly enforce business rules at the controller layer.
* **High-Performance Object Mapping:** Utilized `MapStruct` for compile-time, type-safe mapping between Entities and DTOs, significantly reducing boilerplate code and improving execution speed.
* **Event-Driven Processing:** Utilizes Spring's `ApplicationEventPublisher` to decouple the main appointment booking transaction from the asynchronous email dispatch logic.
* **Schema Validation:** Configured with `ddl-auto=validate` to enforce production-grade schema integrity, relying on pre-defined SQL scripts (`init.sql`) rather than automated Hibernate schema generation.
* **Test-Driven Approach:** Ensured application reliability by writing extensive Unit Tests for both Controller and Service layers using `JUnit 5`, `Mockito`, and `MockMvc`.

---

## Installation & Deployment Guide

The entire development environment is containerized using Docker Compose. There is no requirement to install Java, Maven, or PostgreSQL directly on the host operating system.

### Prerequisites
* Git
* Docker Desktop (Windows/macOS) or Docker Engine with Docker Compose CLI (Linux)

### Execution Steps

1. **Clone the Repository**
```bash
   git clone [https://github.com/nguyensan3108/medical-appointment-system.git](https://github.com/nguyensan3108/medical-appointment-system.git)
   cd medical-appointment-system
   ```

2. **Configure Environment Variables**
  * The application relies on system environment variables to isolate sensitive credentials from the codebase.

  * Copy the template configuration file:
```bash
    cp .env.example .env
   ```
   * Open the newly created `.env` file and replace the placeholders with your actual values (such as your Google App Password for SMTP mail service and a secure encryption key for JWT).

3. **Launch the Containers**
  * Execute the deployment command from the root directory containing the `docker-compose.yml` file:
```bash
   docker compose up --build -d
   ```

4. **Verify Application Logs**
  * To monitor the boot sequence and ensure the database connection pool initiates successfully, stream the container logs:
```bash
  docker compose logs -f app
  ```
  * The application is ready when the log confirms the embedded Tomcat server has started on port 8080.

5. **Access API Documentation**
  * Once the application is running, the interactive OpenAPI/Swagger documentation can be accessed locally at:
   ```http://localhost:8080/swagger-ui/index.html```

6. **Shutdown Command**
  * To stop all running services and dismantle the virtual network without destroying persistent database volumes, run:
```bash
   docker compose down
   ```
