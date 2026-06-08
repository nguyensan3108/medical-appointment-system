# Medical Appointment System - Backend API

A backend API for a Medical Appointment Management System built with Java and Spring Boot. This project implements core features for appointment booking, medical record management, and role-based access control.

## Key Features

* **Architecture:** Strictly follows the Controller-Service-Repository pattern.
* **Database Design:**
    * **Soft Deletes:** Implemented via `@SQLDelete` and `@SQLRestriction` to retain historical data.
    * **Optimistic Locking:** Utilizes `@Version` in `BaseEntity` to handle concurrent booking requests and prevent data overwrites.
    * **Auditing:** Integrated JPA Auditing to automatically track record creation and modification times.
* **Event-Driven Processing:** Uses Spring's `ApplicationEventPublisher` and `TransactionalEventListener` to decouple asynchronous email notifications from the main booking transaction.
* **Testing:** Achieved 100% Line and Branch Coverage for the controller layer using `MockMvc` and `Mockito`.

## Tech Stack

* **Framework:** Java 21, Spring Boot 4.0.x (Spring 7)
* **Database:** PostgreSQL
* **Security:** Spring Security, JWT (JSON Web Tokens), RBAC
* **Testing:** JUnit, Mockito, AssertJ
* **API Documentation:** Springdoc OpenAPI (Swagger UI)

## Local Setup

### 1. Database Configuration
Create a PostgreSQL database named `healthcare_db`. Update the `src/main/resources/application.properties` file with your local database credentials and JWT secret:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/healthcare_db
spring.datasource.username=your_username
spring.datasource.password=your_password

jwt.signerKey=your_jwt_secret_key_must_be_long_enough

spring.mail.username=your_email@gmail.com
spring.mail.password=your_app_password
```

### 2. Build the project
Open a terminal in the project root directory and execute:
```
# Build the project
./mvnw clean package

# Run the application
./mvnw spring-boot:run
```
The server will start by default at `http://localhost:8080.`

## API Documentation
Once the application is running, the interactive Swagger UI documentation can be accessed at:
`http://localhost:8080/swagger-ui/index.html`
