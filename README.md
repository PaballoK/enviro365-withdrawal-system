# Enviro365 Withdrawal Notice System

A full-stack withdrawal management system built for the Enviro365 Investments assessment. Investors can view their portfolios, submit withdrawal notices, and download CSV statements — all enforced against real business rules.

---

## Tech Stack

| Layer | Technology |
|---|---|
| Backend | Java 17, Spring Boot, Spring Data JPA, Spring Validation |
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
│       │   ├── config/          # CORS, ModelMapper config
│       │   ├── controller/      # REST controllers + GlobalExceptionHandler
│       │   ├── dto/             # Request/Response DTOs
│       │   ├── entity/          # JPA entities
│       │   ├── enums/           # ProductType enum
│       │   ├── exception/       # Custom exceptions
│       │   ├── finder/          # Entity lookup utilities
│       │   ├── repository/      # Spring Data repositories
│       │   └── service/         # Business logic (interfaces + impl)
│       └── resources/
│           ├── application.properties
│           └── data.sql         # Seed data
└── frontend/
    └── src/app/
        ├── core/models/         # TypeScript DTOs
        ├── enums/               # ProductType enum
        ├── services/            # HTTP services + InvestorContextService
        ├── shared/              # Sidebar, Loader components
        └── views/               # Dashboard, Withdrawal, History pages
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

| Investor | ID | Birth Date | Age |
|---|---|---|---|
| Sipho Nkosi | 1 | 1955-03-15 | 70 (eligible for retirement withdrawal) |
| Thandi Mokoena | 2 | 1985-07-22 | 40 (not eligible) |
| Pieter van der Merwe | 3 | 1960-11-08 | 65 (eligible) |

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

---

### GET `/api/portfolio/{investorId}`

Retrieves an investor's portfolio including all products and total value.

**Example request:**
```
GET /api/portfolio/1
```

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
- `404 Not Found` — investor does not exist

---

### POST `/api/withdrawals`

Submits a withdrawal notice against a product. Enforces business rules before processing.

**Request body:**
```json
{
  "investorId": 1,
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
  "amount": 50000.00,
  "remainingBalance": 800000.00,
  "processedAt": "2026-06-08T10:30:00"
}
```

**Error responses:**
- `400 Bad Request` — amount exceeds 90% of balance, or investor is under 65 for a retirement product
- `400 Bad Request` — missing or invalid request fields
- `404 Not Found` — investor or product does not exist

---

### GET `/api/withdrawals/investor/{investorId}`

Returns the full withdrawal history for an investor, ordered most recent first.

**Example request:**
```
GET /api/withdrawals/investor/1
```

**Example response (200 OK):**
```json
[
  {
    "id": 1,
    "investorId": 1,
    "productId": 1,
    "productName": "Sipho Retirement Annuity - Old Mutual",
    "amount": 50000.00,
    "remainingBalance": 800000.00,
    "processedAt": "2026-06-08T10:30:00"
  }
]
```

---

### GET `/api/withdrawals/investor/{investorId}/export`

Exports withdrawal history as a downloadable CSV file. Supports optional date range filtering.

**Query parameters:**

| Parameter | Type | Required | Description |
|---|---|---|---|
| `startDate` | `YYYY-MM-DD` | No | Filter from this date (inclusive) |
| `endDate` | `YYYY-MM-DD` | No | Filter to this date (inclusive) |

**Example requests:**
```
GET /api/withdrawals/investor/1/export
GET /api/withdrawals/investor/1/export?startDate=2026-01-01&endDate=2026-06-30
```

**Response:** A CSV file download with columns: ID, Investor ID, Product ID, Product Name, Amount, Remaining Balance, Processed At.

---

## Business Rules

| Rule | Implementation |
|---|---|
| Retirement withdrawals only allowed if investor is 65 or older | `WithdrawalServiceImpl.validateRetirementAge()` using `Period.between()` |
| Withdrawal amount cannot exceed 90% of the product balance | `WithdrawalServiceImpl.validateMaxWithdrawal()` |
| Proper error responses returned for all rule violations | `GlobalExceptionHandler` with `@ControllerAdvice` |

---

## Advanced Features Implemented

- **Global exception handling** — `@ControllerAdvice` with handlers for business exceptions, validation errors, and unexpected errors; all return a consistent `ApiErrorResponse` structure
- **DTO layer** — `WithdrawalRequestDTO`, `WithdrawalResponseDTO`, `InvestorPortfolioDTO`, `ProductDTO`, `ApiErrorResponse` separate the API contract from the domain model
- **Input validation** — `@Valid` + `@NotNull`, `@Positive` on request DTOs; `MethodArgumentNotValidException` handler returns field-level error messages
- **UI validation** — Angular reactive form enforces required fields, minimum amount, and the 90% cap client-side before the request is sent; live max-allowed hint shown to the user

---

## AI Usage Disclosure

This project was built with assistance from **Claude (Anthropic)** via the Claude Code CLI.

AI was used for:
- Scaffolding boilerplate (entity classes, repository interfaces, DTO structure)
- Suggesting the `GlobalExceptionHandler` pattern and `@ControllerAdvice` structure
- Angular component templates and PrimeNG integration
- Identifying and fixing a `ProductType` enum mismatch between the backend `@JsonValue` serialisation and the frontend TypeScript enum

All AI-generated code was reviewed, understood, and validated by the author before inclusion. The business logic (age validation, 90% cap, CSV filtering), architecture decisions (Finder pattern, `InvestorContextService` with `BehaviorSubject`), and overall system design are the author's own.

---

## Screenshots

> Add screenshots of the running application here before submission.

will revert
