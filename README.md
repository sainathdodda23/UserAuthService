# UserAuthService

A JWT-based authentication microservice built using **Java 17, Spring Boot, Spring Security, Spring Data JPA and MySQL**. It provides user registration, login, password encryption and JWT generation and validation for applications that need centralized authentication.

## Overview

UserAuthService is the authentication component of the ShopNest application. It manages user registration and login, securely stores password hashes, and issues JSON Web Tokens (JWT) after successful authentication.

Other microservices can validate a client's JWT by calling the token validation endpoint exposed by this service.

## Features

- User registration using email and password.
- Duplicate email validation.
- Password hashing using BCrypt through Spring Security's `PasswordEncoder`.
- User login with credential verification.
- JWT generation after successful login.
- JWT signature and claims validation.
- Stateless authentication.
- Centralized exception handling and request validation.
- MySQL persistence using Spring Data JPA.
- Production deployment on AWS Elastic Beanstalk.

## Technology Stack

| Component | Technology |
|---|---|
| Language | Java 17 |
| Framework | Spring Boot |
| Security | Spring Security |
| Authentication | JWT (JJWT 0.13.0) |
| Password hashing | BCrypt |
| Persistence | Spring Data JPA / Hibernate |
| Database | MySQL |
| Build tool | Maven |
| API documentation dependency | Springdoc OpenAPI |
| Cloud deployment | AWS Elastic Beanstalk |
| Database hosting | AWS RDS MySQL |

## Architecture

The service follows a layered architecture:

```text
Client / Postman
       |
       v
AuthController
       |
       v
AuthService
       |
       +--------------------+
       |                    |
       v                    v
UserRepository          JwtService
       |
       v
MySQL Database
```

### Main components

- **Controller:** Exposes registration, login and token validation REST endpoints.
- **Service:** Implements registration and authentication business logic.
- **Repository:** Performs user persistence and lookup operations.
- **Security:** Configures stateless security and JWT request filtering.
- **JWT Service:** Generates tokens and parses token claims.
- **DTOs:** Defines request and response payloads.
- **Model:** Contains the User entity and Role enum.

## Authentication Flow

### 1. User Registration

1. Client submits an email and password.
2. The service checks whether the email already exists.
3. Password is encoded using BCrypt.
4. A new user is created with the default role `USER`.
5. The user is saved in MySQL.

### 2. User Login

1. Client submits email and password.
2. The service retrieves the user by email.
3. BCrypt verifies the submitted password against the stored password hash.
4. If credentials are valid, JwtService generates a JWT.
5. The token is returned to the client.

### 3. JWT Validation

1. A client or another microservice sends the JWT in the Authorization header.
2. UserAuthService checks the Bearer token format.
3. JwtService parses and validates the token.
4. The service extracts the subject (email) and role claims.
5. The service returns user information when the token is valid.

## REST API Endpoints

Base path: `/auth`

| Method | Endpoint | Description |
|---|---|---|
| POST | `/auth/signup` | Register a new user |
| POST | `/auth/login` | Authenticate and generate JWT |
| GET | `/auth/validateToken` | Validate JWT and return user details |
| GET | `/auth/hello` | Basic service health/test response |

### 1. Signup

**Request**

```http
POST /auth/signup
Content-Type: application/json
```

```json
{
  "email": "user@example.com",
  "password": "your-password"
}
```

**Successful response**

```json
{
  "id": 1,
  "email": "user@example.com",
  "role": "USER"
}
```

The exact response is determined by the UserDto.

### 2. Login

**Request**

```http
POST /auth/login
Content-Type: application/json
```

```json
{
  "email": "user@example.com",
  "password": "your-password"
}
```

**Successful response**

```json
{
  "token": "<JWT_TOKEN>"
}
```

Invalid credentials return `401 Unauthorized`.

### 3. Validate Token

**Request**

```http
GET /auth/validateToken
Authorization: Bearer <JWT_TOKEN>
```

**Successful response**

```json
{
  "email": "user@example.com",
  "role": "USER"
}
```

The response fields are based on the UserDto returned by the current implementation.

### 4. Hello

```http
GET /auth/hello
```

Returns:

```text
Hello
```

## JWT Details

The service uses JWT for stateless authentication.

A JWT consists of three parts:

1. Header – token algorithm and type.
2. Payload – claims such as subject, role and expiration.
3. Signature – verifies token integrity and authenticity.

The JWT service uses a configured secret to sign tokens. The configured expiration is **900 seconds (15 minutes)**.

The token is sent through the HTTP Authorization header:

```http
Authorization: Bearer <JWT_TOKEN>
```

## Security Configuration

The application uses Spring Security with:

- CSRF disabled for this stateless REST API.
- Session creation policy set to `STATELESS`.
- `/auth/**` endpoints permitted without authentication.
- Other request paths require authentication.
- JWT request filtering before the username/password authentication filter.
- BCrypt password hashing.

## Database Configuration

The service uses MySQL with Spring Data JPA.

The `User` entity stores user information, including:

- User ID
- Email
- Password hash
- Role

Passwords are stored as BCrypt hashes, not as plain text.

## Local Setup

### Prerequisites

- JDK 17
- Maven
- MySQL Server
- IntelliJ IDEA or another Java IDE

### 1. Clone the repository

```bash
git clone https://github.com/sainathdodda23/UserAuthService.git
cd UserAuthService
```

### 2. Configure MySQL

Create a database named:

```sql
CREATE DATABASE userauthservice;
```

Configure the local database settings in `application.properties`.

Set the following environment variables before running:

```text
DB_PASSWORD=<your-local-mysql-password>
JWT_SECRET=<your-secure-jwt-secret>
```

Never commit actual secret values to GitHub.

### 3. Build the application

```bash
mvn clean package
```

### 4. Run the application

```bash
mvn spring-boot:run
```

The local application is configured to run on port `8081`.

## Production Configuration — AWS

The service is deployed using **AWS Elastic Beanstalk** on the Java Corretto 17 platform.

Infrastructure used:

- AWS Elastic Beanstalk – application hosting.
- Amazon EC2 – compute instance managed by Elastic Beanstalk.
- Amazon RDS for MySQL – relational database.
- AWS IAM – service role and EC2 instance profile.
- Amazon CloudWatch Logs – application log monitoring.

The production profile is activated using:

```text
SPRING_PROFILES_ACTIVE=prod
```

Production environment variables:

| Variable | Purpose |
|---|---|
| `DB_URL` | AWS RDS JDBC connection URL |
| `DB_USERNAME` | Database username |
| `DB_PASSWORD` | Database password |
| `JWT_SECRET` | JWT signing secret |
| `PORT` | Application server port, if explicitly configured |

The production configuration is maintained in:

```text
src/main/resources/application-prod.properties
```

Secrets are supplied through environment variables and should not be stored in source code.

## Testing

The APIs can be tested using Postman.

Recommended testing sequence:

1. Register a new user.
2. Attempt registration with the same email.
3. Log in with valid credentials.
4. Attempt login with invalid credentials.
5. Validate the generated JWT.
6. Validate an invalid or expired JWT.

## Future Improvements

- Add refresh token support.
- Add role-based authorization policies.
- Add password reset functionality.
- Add account verification through email.
- Add rate limiting for authentication endpoints.
- Improve automated unit and integration test coverage.
- Integrate centralized logging and monitoring.

## Author

Sainath Dodda

GitHub: https://github.com/sainathdodda23
