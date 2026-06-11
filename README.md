# Enviro365 Withdrawal Notice System

A full-stack investment transaction system built for the Enviro365 Investments assessment. Investors can view their portfolios, submit withdrawal notices, make deposits, and download CSV statements — all enforced against real business rules.

---

## Tech Stack

| Layer | Technology |
|---|---|
| Backend | Java 17, Spring Boot, Spring Data JPA, Spring Validation, Spring Security |
| Auth | JWT (JJWT), BCrypt password hashing |
| Database | H2 (in-memory) |
| Frontend | Angular 17 (standalone components), PrimeNG, RxJS |
| CSV Export | Apache Commons CSV |
| Mapping | ModelMapper |

---

## Project Structure

```
enviro365-withdrawal-system-backend/
├── src/
│   └── main/
│       ├── java/com/enviro/assessment/junior/paballo/
│       │   ├── annotations/     # @CurrentUser annotation + argument resolver
│       │   ├── config/          # SecurityConfig, JwtAuthenticationFilter, WebConfig
│       │   ├── controller/      # REST controllers + GlobalExceptionHandler
│       │   ├── dto/             # Request/Response DTOs + auth DTOs
│       │   ├── entity/          # JPA entities (Investor implements UserDetails)
│       │   ├── enums/           # ProductType, TransactionType enums
│       │   ├── exception/       # Custom exceptions
│       │   ├── repository/      # Spring Data repositories
│       │   └── service/         # Business logic + AuthenticationService + JwtService
│       └── resources/
│           ├── application.properties
│           └── data.sql         # Seed data with bcrypt-hashed passwords
└── frontend/
    └── src/app/
        ├── core/models/         # TypeScript DTOs
        ├── enums/               # ProductType enum
        ├── guards/              # AuthGuard (CanActivateFn)
        ├── interceptors/        # JWT interceptor (HttpInterceptorFn)
        ├── services/            # HTTP services + AuthService
        ├── shared/              # Sidebar, Loader components
        └── views/               # Login, Dashboard, Withdrawal, History pages
```

---

## Prerequisites

- Java 17+
- Maven 3.8+
- Node.js 18+ and npm

---

## Setup & Running

### 1. Backend

```bash
# From the project root
./mvnw spring-boot:run
```

The API will start on `http://localhost:8080`.

The H2 console is available at `http://localhost:8080/h2-console`:
- JDBC URL: `jdbc:h2:mem:enviro365db`
- Username: `sa`
- Password: *(leave blank)*

### 2. Frontend

```bash
cd frontend
npm install
ng serve
```

The UI will be available at `http://localhost:4200`.

> Start the backend before the frontend — the UI calls `localhost:8080` directly.

---

## Seed Data

The database is seeded automatically on startup via `data.sql`.

| Investor | Email | Password | Birth Date | Retirement Eligible |
|---|---|---|---|---|
| Sipho Nkosi | sipho.nkosi@gmail.com | password123 | 1955-03-15 | Yes (age 70) |
| Thandi Mokoena | thandi.mokoena@gmail.com | password123 | 1985-07-22 | No (age 40) |
| Pieter van der Merwe | pieter.vdm@gmail.com | password123 | 1960-11-08 | Yes (age 65) |

| Product | ID | Type | Balance | Investor |
|---|---|---|---|---|
| Sipho Retirement Annuity - Old Mutual | 1 | RETIREMENT | R850,000 | Sipho |
| Sipho Tax-Free Savings Account - FNB | 2 | SAVINGS | R120,000 | Sipho |
| Thandi Retirement Portfolio - Allan Gray | 3 | RETIREMENT | R95,000 | Thandi |
| Thandi Easy Save Account - Standard Bank | 4 | SAVINGS | R45,000 | Thandi |
| Pieter Pension Fund - Sanlam | 5 | RETIREMENT | R620,000 | Pieter |
| Pieter Balanced Investment Portfolio - PSG Wealth | 6 | INVESTMENT | R180,000 | Pieter |

---

## API Documentation

Base URL: `http://localhost:8080`

All error responses follow this structure:

```json
{
  "code": 400,
  "status": "Bad Request",
  "message": "Descriptive error message",
  "timestamp": "2026-06-08T10:00:00"
}
```

Protected endpoints require a `Bearer` token in the `Authorization` header:
```
Authorization: Bearer <jwt-token>
```

---

### POST `/api/auth/login` — Public

Authenticates an investor and returns a JWT.

**Request body:**
```json
{
  "email": "sipho.nkosi@gmail.com",
  "password": "password123"
}
```

**Example response (200 OK):**
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9..."
}
```

**Error responses:**
- `401 Unauthorized` — invalid email or password

---

### GET `/api/portfolio` — Protected

Returns the authenticated investor's portfolio including all products and total value. Investor identity is resolved from the JWT — no ID in the URL.

**Example response (200 OK):**
```json
{
  "investorId": 1,
  "firstName": "Sipho",
  "lastName": "Nkosi",
  "email": "sipho.nkosi@gmail.com",
  "products": [
    {
      "id": 1,
      "productName": "Sipho Retirement Annuity - Old Mutual",
      "productType": "Retirement",
      "balance": 850000.00
    },
    {
      "id": 2,
      "productName": "Sipho Tax-Free Savings Account - FNB",
      "productType": "Savings",
      "balance": 120000.00
    }
  ],
  "totalValue": 970000.00
}
```

**Error responses:**
- `401 Unauthorized` — missing or invalid token

---

### POST `/api/withdrawals` — Protected

Submits a withdrawal notice against a product. The investor is taken from the JWT, not the request body.

**Request body:**
```json
{
  "productId": 1,
  "withdrawalAmount": 50000.00
}
```

**Example response (201 Created):**
```json
{
  "id": 1,
  "investorId": 1,
  "productId": 1,
  "productName": "Sipho Retirement Annuity - Old Mutual",
  "type": "WITHDRAWAL",
  "amount": 50000.00,
  "balanceAfter": 800000.00,
  "processedAt": "2026-06-08T10:30:00"
}
```

**Error responses:**
- `400 Bad Request` — amount exceeds 90% of balance, or investor is under 65 for a retirement product
- `400 Bad Request` — missing or invalid request fields
- `401 Unauthorized` — missing or invalid token
- `404 Not Found` — product does not exist

---

### GET `/api/withdrawals/history` — Protected

Returns the authenticated investor's full transaction history, ordered most recent first. Optionally filter by transaction type.

**Query parameters:**

| Parameter | Type | Required | Description |
|---|---|---|---|
| `type` | `WITHDRAWAL` \| `DEPOSIT` | No | Filter by transaction type; omit for all |

**Example requests:**
```
GET /api/withdrawals/history
GET /api/withdrawals/history?type=WITHDRAWAL
```

**Example response (200 OK):**
```json
[
  {
    "id": 1,
    "investorId": 1,
    "productId": 1,
    "productName": "Sipho Retirement Annuity - Old Mutual",
    "type": "WITHDRAWAL",
    "amount": 50000.00,
    "balanceAfter": 800000.00,
    "processedAt": "2026-06-08T10:30:00"
  }
]
```

---

### GET `/api/withdrawals/export` — Protected

Exports the authenticated investor's withdrawal history as a downloadable CSV file. Supports optional date range filtering.

**Query parameters:**

| Parameter | Type | Required | Description |
|---|---|---|---|
| `startDate` | `YYYY-MM-DD` | No | Filter from this date (inclusive) |
| `endDate` | `YYYY-MM-DD` | No | Filter to this date (inclusive) |

**Example requests:**
```
GET /api/withdrawals/export
GET /api/withdrawals/export?startDate=2026-01-01&endDate=2026-06-30
```

**Response:** A CSV file download with columns: ID, Investor ID, Product ID, Product Name, Amount, Balance After, Processed At.

---

## Business Rules

| Rule | Implementation |
|---|---|
| Retirement withdrawals only allowed if investor is 65 or older | `TransactionServiceImpl.validateRetirementAge()` using `Period.between()` |
| Withdrawal amount cannot exceed 90% of the product balance | `TransactionServiceImpl.validateMaxWithdrawal()` |
| Proper error responses returned for all rule violations | `GlobalExceptionHandler` with `@ControllerAdvice` |

---

## Advanced Features Implemented

- **Spring Security + JWT** — stateless authentication via JJWT; `JwtAuthenticationFilter` validates the Bearer token on every request and populates the `SecurityContext`
- **`@CurrentUser` annotation** — custom `HandlerMethodArgumentResolver` injects the authenticated `Investor` directly into controller method parameters, keeping controllers free of `SecurityContextHolder` calls
- **BCrypt password hashing** — passwords stored as bcrypt hashes; `BCryptPasswordEncoder` used for both storage and verification
- **Global exception handling** — `@ControllerAdvice` with handlers for business exceptions, validation errors, `ResponseStatusException`, and unexpected errors; all return a consistent `ApiErrorResponse` structure
- **DTO layer** — `WithdrawalRequestDTO`, `DepositRequestDTO`, `TransactionResponseDTO`, `InvestorPortfolioDTO`, `ProductDTO`, `ApiErrorResponse` separate the API contract from the domain model
- **Input validation** — `@Valid` + `@NotNull`, `@Positive` on request DTOs; `MethodArgumentNotValidException` handler returns field-level error messages
- **UI validation** — Angular reactive form enforces required fields, minimum amount, and the 90% cap client-side before the request is sent; live max-allowed hint shown to the user
- **Angular auth integration** — `AuthGuard` (functional `CanActivateFn`) protects all routes; `jwtInterceptor` (`HttpInterceptorFn`) automatically attaches the Bearer token to every outbound request

---

## AI Usage Disclosure

This project was built with assistance from **Claude (Anthropic)** via the Claude Code CLI.

AI was used for:
- Scaffolding boilerplate (entity classes, repository interfaces, DTO structure)
- Suggesting the `GlobalExceptionHandler` pattern and `@ControllerAdvice` structure
- Angular component templates and PrimeNG integration
- Identifying and fixing a `ProductType` enum mismatch between the backend `@JsonValue` serialisation and the frontend TypeScript enum
- Implementing the Spring Security + JWT layer: `SecurityConfig`, `JwtAuthenticationFilter`, `JwtService`, `@CurrentUser` annotation + resolver, and the Angular `AuthGuard` / `jwtInterceptor`

All AI-generated code was reviewed, understood, and validated by me before inclusion. The business logic (age validation, 90% cap, CSV filtering), architecture decisions, and overall system design are my own.

---

## Screenshots

><img width="1440" height="900" alt="Screenshot 2026-06-09 at 16 42 47" src="https://github.com/user-attachments/assets/d8544ad5-89bd-4171-9163-9c1b287a5d7d" />

<img width="1440" height="820" alt="Screenshot 2026-06-09 at 16 38 55" src="https://github.com/user-attachments/assets/8150906b-f3dd-4175-a304-1aa93f607930" />

<img width="1440" height="900" alt="Screenshot 2026-06-09 at 16 43 03" src="https://github.com/user-attachments/assets/9b36f739-e219-4db9-88c7-a1c2ae9681eb" />

<img width="1440" height="900" alt="Screenshot 2026-06-09 at 16 43 42" src="https://github.com/user-attachments/assets/73c18d5f-aba4-44c6-aec7-4b802afe7361" />
