# Clinic Service

## Overview

Clinic Service is a Spring Boot application that keeps track of clinics, their specialties, contact information, and subscription end dates. It exposes REST endpoints secured by Jakarta Bean Validation, stores data via Spring Data JPA, and ships with separate configuration profiles for development (H2) and production (MySQL).

## Architecture Snapshot

- **Entry point**: `ClinicController` receives HTTP requests and applies request validation.
- **Service layer**: `ClinicService` encapsulates activation rules, expiration lookups, and CRUD orchestration.
- **Mapping**: `ClinicMapper` converts between DTOs and the `Clinic` JPA entity.
- **Persistence**: `ClinicRepository` leverages Spring Data JPA for database access.
- **Error handling**: `GlobalExceptionHandler` translates domain exceptions into HTTP responses.

## API Reference

### Endpoint Summary

| Method & Path | Purpose | Required Inputs | Success Response |
| --- | --- | --- | --- |
| `POST /api/clinics` | Create a clinic record. | Body with clinic attributes. | `200 OK` → created clinic DTO. |
| `PUT /api/clinics/{id}` | Replace clinic data. | Path `id`, body with clinic attributes. | `200 OK` → updated clinic DTO. |
| `PATCH /api/clinics/{id}/activate` | Set clinic status to `ACTIVE`. | Path `id`. | `200 OK` → updated clinic DTO. |
| `PATCH /api/clinics/{id}/deactivate` | Set clinic status to `INACTIVE`. | Path `id`. | `200 OK` → updated clinic DTO. |
| `GET /api/clinics/{id}` | Retrieve a clinic by id. | Path `id`. | `200 OK` → clinic DTO. |
| `GET /api/clinics` | List all clinics. | None. | `200 OK` → array of clinic DTOs. |
| `GET /api/clinics/active` | List only active clinics. | None. | `200 OK` → array filtered by status. |
| `GET /api/clinics/near-expiration` | List clinics expiring soon. | Query `daysBefore` (default `30`). | `200 OK` → array filtered by service end date. |

### Detailed Contracts

#### Create Clinic

- **Method/Path**: `POST /api/clinics`
- **Body Fields**:
  - `name` *(string, required)* – clinic name.
  - `specialty` *(string, required)* – medical specialty or service focus.
  - `phone` *(string, required)* – must match `\d{10}`.
  - `address` *(string, required)* – physical address.
  - `logoUrl` *(string, optional)* – CDN or storage URL for logo.
  - `status` *(enum, optional)* – `ACTIVE` (default) or `INACTIVE`.
  - `serviceEndDate` *(ISO date, required)* – contract/subscription end date.
- **Responses**: `200 OK` with created clinic DTO, `400 Bad Request` on validation issues.

#### Update Clinic

- **Method/Path**: `PUT /api/clinics/{id}`
- **Path Params**: `id` *(long, required)* – identifier of the clinic to update.
- **Body**: same schema as creation request.
- **Responses**: `200 OK` updated clinic, `404 Not Found` when `id` does not exist.

#### Activate/Deactivate Clinic

- **Method/Path**: `PATCH /api/clinics/{id}/activate` or `/deactivate`
- **Path Params**: `id` *(long, required)*.
- **Responses**: `200 OK` with updated clinic DTO, `404 Not Found` if `id` missing.

#### Fetch Clinic By Id

- **Method/Path**: `GET /api/clinics/{id}`
- **Responses**: `200 OK` clinic DTO, `404 Not Found` when unavailable.

#### List Clinics

- **Method/Path**: `GET /api/clinics`
- **Responses**: `200 OK` array containing every clinic.

#### List Active Clinics

- **Method/Path**: `GET /api/clinics/active`
- **Responses**: `200 OK` array filtered where `status = ACTIVE`.

#### List Clinics Near Expiration

- **Method/Path**: `GET /api/clinics/near-expiration`
- **Query Params**: `daysBefore` *(integer, optional, default 30)* – number of days from today defining the cutoff.
- **Responses**: `200 OK` array with `serviceEndDate` before `now + daysBefore`.

### Response JSON Example

```json
{
  "name": "Cabinet Alpha",
  "specialty": "Dermatology",
  "phone": "0611223344",
  "address": "123 Avenue, Paris",
  "logoUrl": "https://cdn.example/logo.png",
  "status": "ACTIVE",
  "serviceEndDate": "2024-12-31"
}
```

## Running the Service

### Local Development

1. Install JDK 17+ and Docker (optional).
2. Clone the repository and switch to this module.
3. Run the service:

```sh
./mvnw clean spring-boot:run
```

Application starts on `http://localhost:8085` using the `dev` profile and the in-memory H2 database.

### Docker Image (Buildpacks)

Use Spring Boot buildpacks to create an OCI image without crafting a Dockerfile:

```sh
./mvnw spring-boot:build-image -Dspring-boot.build-image.imageName=clinic-service:latest
docker run --rm -p 8085:8085 clinic-service:latest
```

## Configuration Profiles

- `application.yml` – shared defaults.
- `application-dev.yml` – dev profile (H2, port 8085).
- `application-prod.yml` – prod profile (MySQL credentials placeholder).

Activate a profile with `SPRING_PROFILES_ACTIVE=dev|prod` or `--spring.profiles.active=dev`.

## Project Structure

```text
src/
  main/java/com/gi/clinicservice/
    controller/ClinicController.java
    service/ClinicService.java
    service/impl/ClinicServiceImpl.java
    mapper/ClinicMapper.java
    repository/ClinicRepository.java
    model/{dto,entity,enums}
    exception/
  resources/
    application.yml
    application-dev.yml
    application-prod.yml
    messages/
```

Tests live under `src/test/java` with a Spring context smoke test (`ClinicServiceApplicationTests`).
