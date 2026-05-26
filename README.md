# AegisFlow AI

Enterprise-grade financial reconciliation and transaction intelligence platform built using Spring Boot, PostgreSQL, Flyway, Docker, and JWT-based RBAC authentication.


---

## Overview

AegisFlow AI is a scalable backend platform designed for enterprise financial operations including transaction ingestion, reconciliation workflows, audit-ready processing, secure authentication, and role-based access control.

The system architecture is inspired by enterprise fintech reconciliation platforms used in banking, payment systems, and operational finance environments.

---

# Architecture

```text
                           ┌─────────────────────┐
                           │   Client Apps/UI    │
                           └─────────┬───────────┘
                                     │
                                     ▼
                    ┌────────────────────────────────┐
                    │      Spring Boot REST API      │
                    └────────────────────────────────┘
                                     │
        ┌────────────────────────────┼────────────────────────────┐
        │                            │                            │
        ▼                            ▼                            ▼

┌─────────────────┐      ┌────────────────────┐      ┌────────────────────┐
│ Authentication  │      │ Transaction Engine │      │ Reconciliation     │
│ & Authorization │      │                    │      │ Engine             │
└─────────────────┘      └────────────────────┘      └────────────────────┘
        │                            │                            │
        ▼                            ▼                            ▼

• JWT Authentication       • CSV Upload APIs          • Rule Execution
• Refresh Tokens           • Validation Pipeline      • Match Detection
• RBAC Permissions         • Error Tracking           • Exception Handling
• Secure Sessions          • Job Monitoring           • Statistics Engine

        └────────────────────────────┼────────────────────────────┘
                                     │
                                     ▼

                    ┌────────────────────────────────┐
                    │        PostgreSQL Database      │
                    └────────────────────────────────┘
                                     │
         ┌───────────────┬───────────────┬───────────────┐
         ▼               ▼               ▼               ▼

     Identity         Transactions    Reconciliation     Audit
      Schema             Schema          Schema          Schema
```

---

# Tech Stack

## Backend
- Java 21
- Spring Boot 4
- Spring Security
- Spring Data JPA
- Hibernate

## Database
- PostgreSQL 16
- Flyway Migration Engine

## Infrastructure
- Docker
- Docker Compose
- Maven

## Security
- JWT Authentication
- Refresh Token Rotation
- RBAC Authorization

## API Documentation
- Swagger / OpenAPI 3

---

# Features

- Enterprise-grade backend architecture
- JWT authentication and refresh tokens
- Role-Based Access Control (RBAC)
- Financial transaction APIs
- CSV transaction ingestion pipeline
- Reconciliation rule engine
- Match and exception tracking
- Audit-oriented database structure
- Flyway schema versioning
- Dockerized PostgreSQL environment
- Swagger/OpenAPI documentation

---

# API Modules

## Authentication APIs

```http
POST /api/v1/auth/register
POST /api/v1/auth/login
POST /api/v1/auth/refresh
POST /api/v1/auth/logout
POST /api/v1/auth/logout-all
```

## Transaction APIs

```http
GET /api/v1/transactions
GET /api/v1/transactions/{transactionId}
```

## Reconciliation APIs

```http
GET  /api/v1/reconciliation/rules
POST /api/v1/reconciliation/rules

GET  /api/v1/reconciliation/jobs
GET  /api/v1/reconciliation/jobs/{jobId}
GET  /api/v1/reconciliation/jobs/{jobId}/statistics
GET  /api/v1/reconciliation/jobs/{jobId}/matches
GET  /api/v1/reconciliation/jobs/{jobId}/exceptions

POST /api/v1/reconciliation/jobs/run
```

## Transaction Ingestion APIs

```http
POST /api/v1/transaction-ingestions/csv
GET  /api/v1/transaction-ingestions/{jobId}
GET  /api/v1/transaction-ingestions/{jobId}/errors
```

---

# Project Structure

```text
src/main/java/com/aegisflow/api
│
├── auth
├── config
├── reconciliation
├── transaction
├── ingestion
├── security
├── audit
├── common
└── infrastructure
```

---

# Running The Project

## Clone Repository

```bash
git clone https://github.com/SAMREEN111-hash/AegisFlow-AI.git
cd AegisFlow-AI
```

## Start PostgreSQL

```bash
docker compose up -d
```

## Run Database Migrations

```bash
mvn flyway:migrate
```

## Start Application

```bash
mvn spring-boot:run
```

---

# Swagger Documentation

```text
http://localhost:8080/swagger-ui/index.html
```

---

# Security Model

The platform implements enterprise RBAC permissions including:

- USER_ADMIN
- TRANSACTION_READ
- TRANSACTION_INGEST
- RECONCILIATION_RUN
- RECONCILIATION_APPROVE
- AUDIT_READ

JWT access and refresh tokens are issued during authentication and validated across secured endpoints.

---

# Author

Samreen Shaikh

GitHub:
https://github.com/SAMREEN111-hash
