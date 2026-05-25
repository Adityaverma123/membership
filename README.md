# FirstClub Membership Service

Simple Java + Spring Boot 3 backend for tiered membership subscriptions.

## Stack

- Java 17, Spring Boot 3, Spring MVC
- H2, JPA/Hibernate, Flyway
- OpenAPI-first contract in [spec.yaml](/Users/adityaverma/Projects/membership-program/spec.yaml)
- OpenAPI Generator for API interfaces and DTO models
- Gradle

## Build 
The build automatically generates OpenAPI server interfaces and DTO models from spec.yaml.
./gradlew clean build
Generated sources are created during build using OpenAPI Generator.

## OpenAPI-First Development

The API contract is maintained in: [spec.yaml](/Users/adityaverma/Projects/membership-program/spec.yaml):
This project uses:

OpenAPI Generator
interface-first Spring APIs
generated DTO models/interfaces

Generated code includes:

API interfaces
request/response DTOs
OpenAPI models

Manual generation (optional):
```bash
./gradlew openApiGenerate
```
Normally this is NOT required because generation already runs during build.
## Run Locally

Run the service:

```bash
./gradlew bootRun
```

The service runs at:

- Swagger UI: `http://localhost:8080/swagger-ui.html`
- OpenAPI JSON: `http://localhost:8080/v3/api-docs`
- H2 Console: `http://localhost:8080/h2-console`

H2 console settings:

```text
JDBC URL: jdbc:h2:mem:membership;MODE=PostgreSQL;DATABASE_TO_LOWER=TRUE;DEFAULT_NULL_ORDERING=HIGH;NON_KEYWORDS=YEAR,MONTH
User Name: sa
Password:
```

## Useful APIs

```bash
curl http://localhost:8080/membership/v1/plans
curl http://localhost:8080/membership/v1/tiers
```

Subscribe:

```bash
curl -X POST http://localhost:8080/membership/v1/users/subscribe \
  -H 'Content-Type: application/json' \
  -H 'X-User-Id: user-1' \
  -d '{"membershipPlanId":1}'
```

Simulate orders:

```bash
curl -X POST http://localhost:8080/membership/v1/internal/orders \
  -H 'Content-Type: application/json' \
  -H 'X-User-Id: user-1' \
  -d '{"orderValue":2500}'
```

Evaluate tier:

```bash
curl -X POST http://localhost:8080/membership/v1/users/evaluate-tier \
  -H 'X-User-Id: user-1'
```

Cancel:

```bash
curl -X POST http://localhost:8080/membership/v1/users/cancel \
  -H 'X-User-Id: user-1'
```

Flyway migrations live in [src/main/resources/db/migration](/Users/adityaverma/Projects/membership-program/src/main/resources/db/migration).

## Design Notes || Assumptions

- Plans are purchased and tiers are earned
- Tiers are computed from configurable tier rules.
- Raw orders are not stored.
- Internal order ingestion api updates the monthly aggregate which mocks order event.
- Cohort matching is intentionally represented as a small resolver in `MembershipService`; it can be replaced by a user/cohort service later without changing tier rule storage.
