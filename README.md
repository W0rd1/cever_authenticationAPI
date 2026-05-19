# 📘 Spring Boot 4 Security API

A secure, production-ready REST API built with **Spring Boot 4**, implementing **JWT authentication**, **account lifecycle management**, and **admin-controlled user access**. This backend is designed to integrate seamlessly with modern frontends (Angular 21+, React, etc.).

---

## 🚀 Tech Stack

* Java 21
* Spring Boot 3.x
* Spring Security (JWT-based auth)
* Spring Data JPA
* PostgreSQL
* Maven
* Lombok
* BCrypt Password Encoding

---

## 🧱 Architecture Overview

This API follows a layered architecture:

```
Controller → Service → Repository → Database
```

Security is enforced at:

* Authentication filter (JWT validation)
* Authorization layer (Spring Security rules)
* Account state checks (enabled / locked)

---

## 🔐 Security Model

### User Account States

| Field                | Type    | Description                         |
| -------------------- | ------- | ----------------------------------- |
| `enabled`            | boolean | Account must be activated via email |
| `accountNonLocked`   | boolean | Locked after failed login attempts  |
| `failedAttemptCount` | int     | Triggers lock at ≥ 3 attempts       |

---

### Authentication Flow

1. User registers → account is `enabled = false`
2. Activation email sent with token
3. User activates account via `/auth/activate`
4. Login issues JWT token
5. JWT used for all protected routes

---

## 📦 Base URL

```
http://localhost:8080/api
```

---

## 📡 API Endpoints

---

## 🔑 Authentication Controller

### Register User

```http
POST /auth/register
```

**Request:**

```json
{
  "username": "string",
  "email": "string",
  "password": "string"
}
```

**Response:**

* `201 Created` → Registration successful (activation email sent)

---

### Login

```http
POST /auth/login
```

**Request:**

```json
{
  "username": "string",
  "password": "string"
}
```

**Responses:**

| Status           | Meaning               |
| ---------------- | --------------------- |
| 200 OK           | JWT returned          |
| 401 Unauthorized | Invalid credentials   |
| 403 Forbidden    | Account not activated |
| 423 Locked       | Account locked        |

**Success Response:**

```json
{
  "token": "jwt-token"
}
```

---

### Activate Account

```http
POST /auth/activate
```

**Request:**

```json
{
  "token": "string"
}
```

---

### Forgot Password

```http
POST /auth/forgot-password
```

```json
{
  "email": "string"
}
```

---

### Reset Password

```http
POST /auth/reset-password
```

```json
{
  "token": "string",
  "newPassword": "string"
}
```

---

## 🛡 Admin Controller

---

### Get All Users

```http
GET /admin/users
```

**Headers:**

```
Authorization: Bearer <JWT>
```

---

### Lock / Unlock User Account

```http
PUT /admin/users/lock-status
```

**Request:**

```json
{
  "username": "string",
  "lock": true
}
```

---

## 🔐 JWT Security

### Token Format

```
Authorization: Bearer <token>
```

### Token Features

* Stateless authentication
* Signed using secret key (HS256 or RS256)
* Expiration enforced
* Validated via Spring Security filter chain

---

## 🧠 Business Rules

### Registration

* New users are created with:

  * `enabled = false`
  * `accountNonLocked = true`

### Login Rules

| Condition                | Result                    |
| ------------------------ | ------------------------- |
| Wrong credentials        | increment failed attempts |
| failedAttemptCount ≥ 3   | account locked            |
| enabled = false          | 403 Forbidden             |
| accountNonLocked = false | 423 Locked                |

---

## 🗄️ Database Schema (Simplified)

### User Table

```sql
id BIGINT PRIMARY KEY
username VARCHAR UNIQUE
email VARCHAR UNIQUE
password VARCHAR
enabled BOOLEAN
account_non_locked BOOLEAN
failed_attempt_count INT
```

---

## 📧 Email Features

* Account activation email
* Password reset email
* Token-based secure links

---

## 🔒 Spring Security Configuration

Key features:

* Stateless session policy
* JWT authentication filter
* Endpoint-level authorization
* Public endpoints:

  * `/auth/**`
* Protected endpoints:

  * `/admin/**`

---

## ⚙️ Running the Project

### Prerequisites

* Java 17+
* Maven
* PostgreSQL

---

### Run locally

```bash
mvn clean install
mvn spring-boot:run
```

---

## 🧪 Example cURL

### Login

```bash
curl -X POST http://localhost:8080/api/auth/login \
-H "Content-Type: application/json" \
-d '{"username":"admin","password":"admin123"}'
```

---

## 📈 Future Improvements

* Refresh token rotation
* OAuth2 / Social login (Google, GitHub)
* Role-based access control (RBAC)
* Audit logging system
* Rate limiting (login protection)
* Multi-factor authentication (MFA)
* Redis session blacklist for JWT revocation

---

## 🧑‍💼 Frontend Integration

Compatible with:

* Angular 21+ (Standalone + Signals architecture)
* React / Next.js
* Vue 3
* Any REST-based SPA

---

## 📄 License

Internal enterprise system — proprietary use only.

---

## ⭐ Summary

This backend provides:

* Secure JWT authentication
* Full account lifecycle management
* Admin-controlled user access
* Production-ready Spring Security architecture
* Clean REST API for modern frontend integration

---

If you want next step, I can also generate:

* 🔥 Spring Security full config class (JWT filter chain)
* 🔥 Entity + repository + service layer
* 🔥 Email token system implementation
* 🔥 Refresh token + Redis blacklist system
* 🔥 Dockerized production setup (Postgres + Spring Boot)

Just tell me.

