
# Analytics Service

## Service Overview

The Analytics Service provides clinic-specific and global KPI snapshots sourced from persisted statistics records. It centralizes KPI retrieval for other services or dashboards, exposing REST endpoints for querying date-bound metrics.

## API Endpoints

| Method | Path | Purpose | Inputs | Outputs |
| --- | --- | --- | --- | --- |
| GET | `/api/statistics/clinic/{clinicId}/kpis` | Fetch KPI history for a clinic. | **Path**: `clinicId` (Long) <br> **Query**: `startDate` (ISO date, optional), `endDate` (ISO date, optional), `period` (`DAY\|WEEK\|MONTH`, optional). | `200 OK` with array of objects:<br>`[{ "date": "2024-05-01", "numberOfConsultations": 42, "numberOfNewPatients": 3, "revenue": 1234.56 }]` |
| GET | `/api/statistics/global/kpis` | Fetch global KPI history. | **Query**: `startDate`, `endDate`, `period` (all optional, same semantics). | `200 OK` with array of objects:<br>`[{ "date": "2024-05-01", "numberOfActiveClinics": 10, "totalNumberOfUsers": 450, "totalNumberOfAppointments": 320 }]` |

_Notes_: All parameters are optional; when omitted the service currently passes `null` to repositories (no defaulting). Responses are JSON arrays; empty arrays indicate no data. No non-200 responses are explicitly defined beyond Spring defaults.

### Request / Response Examples

#### Clinic KPIs

```http
GET /api/statistics/clinic/42/kpis?startDate=2025-01-01&endDate=2025-01-07&period=DAY
Host: localhost:8089
Accept: application/json
```

```http
HTTP/1.1 200 OK
Content-Type: application/json

[
   {
      "date": "2025-01-01",
      "numberOfConsultations": 18,
      "numberOfNewPatients": 4,
      "revenue": 2450.75
   },
   {
      "date": "2025-01-02",
      "numberOfConsultations": 22,
      "numberOfNewPatients": 3,
      "revenue": 2680.10
   }
]
```

#### Global KPIs

```http
GET /api/statistics/global/kpis?startDate=2025-01-01&endDate=2025-01-31&period=WEEK
Host: localhost:8089
Accept: application/json
```

```http
HTTP/1.1 200 OK
Content-Type: application/json

[
   {
      "date": "2025-01-05",
      "numberOfActiveClinics": 58,
      "totalNumberOfUsers": 1820,
      "totalNumberOfAppointments": 1140
   },
   {
      "date": "2025-01-12",
      "numberOfActiveClinics": 60,
      "totalNumberOfUsers": 1875,
      "totalNumberOfAppointments": 1188
   }
]
```

## How to Run

### Prerequisites

- Java 21+
- Maven 3.9+ (or included wrapper)
- Optional: Docker (for database overrides)

### Local Execution

```sh
./mvnw spring-boot:run
```

The application starts on port `8089` and uses the in-memory H2 database configured in [src/main/resources/application.yml](src/main/resources/application.yml).

### Packaging

```sh
./mvnw clean package
```

Runnable JAR: `target/analytics-service-0.0.1-SNAPSHOT.jar`.

### Docker (optional)

1. Build image:

   ```sh
   ./mvnw spring-boot:build-image -Dspring-boot.build-image.imageName=analytics-service:latest
   ```

2. Run:

   ```sh
   docker run -e SPRING_PROFILES_ACTIVE=dev -p 8089:8089 analytics-service:latest
   ```

Configure external databases via environment variables or mounted `application.yml`.
