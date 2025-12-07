# Clinic Service Overview

## 1. Introduction
Clinic Service is a RESTful Spring Boot microservice responsible for storing, updating, and surfacing metadata about medical clinics (name, specialty, contact data, subscription end date, and activation state). The service follows a classic layered architecture: HTTP controllers marshal requests into DTOs, a service layer enforces business rules, mappers translate between DTOs and persistence entities, and a Spring Data JPA repository persists the entities in H2 (dev) or MySQL (prod).

## 2. Architecture Overview

- **Frameworks**: Spring Boot 3, Spring Web MVC, Spring Data JPA, Jakarta Bean Validation, Lombok.
- **Data Model**: Single aggregate (`Clinic`) stored in a relational database.
- **Validation**: Performed via annotations declared on `ClinicDTO`, executed before controller methods run.
- **Error Handling**: Custom runtime exceptions intercepted by `GlobalExceptionHandler` to emit 4xx responses.
- **Configuration Profiles**: `dev` uses in-memory H2, `prod` expects a MySQL datasource (credentials to be supplied externally).

## 3. Class-by-Class Analysis

### [com.gi.clinicservice.ClinicServiceApplication](../src/main/java/com/gi/clinicservice/ClinicServiceApplication.java)

- **Responsibility**: Service bootstrapper. Declares `@SpringBootApplication`, enabling component scanning for the `com.gi.clinicservice` package.
- **Key Methods**: `main` delegates to `SpringApplication.run`, which wires controllers, services, repositories, and infrastructure beans.
- **Dependencies**: Spring Boot autoconfiguration; no direct dependencies on project classes.
- **Business Logic**: None.

### [com.gi.clinicservice.controller.ClinicController](../src/main/java/com/gi/clinicservice/controller/ClinicController.java)

- **Responsibility**: Public HTTP interface under `/api/clinics`.
- **Key Methods & Semantics**:
  - `createClinic` (`POST /api/clinics`): Validates payload, delegates to `ClinicService.createClinic`.
  - `updateClinic` (`PUT /api/clinics/{id}`): Passes ID and DTO for a full update.
  - `activateClinic` / `deactivateClinic` (`PATCH .../activate|deactivate`): Toggle status without request body.
  - `findById`, `findAll`, `findActive`, `findNearExpiration`: Query operations returning DTO lists or single DTO.
- **Dependencies**: Injects [`ClinicService`](../src/main/java/com/gi/clinicservice/service/ClinicService.java). Uses `@Valid` so DTO constraints run automatically.
- **Business Logic**: Thin delegation layer; does not manipulate data beyond validation and `ResponseEntity` creation.

### [com.gi.clinicservice.service.ClinicService](../src/main/java/com/gi/clinicservice/service/ClinicService.java)

- **Responsibility**: Defines the domain contract for clinic management operations.
- **Key Methods**:
  - `createClinic` / `updateClinic`: Persist or modify clinic metadata.
  - `activateClinic` / `deactivateClinic`: Transition state between `ACTIVE` and `INACTIVE`.
  - `findById`, `findAll`, `findActive`: Retrieval operations.
  - `findNearExpiration`: Fetch clinics whose `serviceEndDate` is approaching.
- **Dependencies**: None directly; implemented by `ClinicServiceImpl`.
- **Business Logic**: Interface only, but defines boundaries for higher layers.

### [com.gi.clinicservice.service.impl.ClinicServiceImpl](../src/main/java/com/gi/clinicservice/service/impl/ClinicServiceImpl.java)

- **Responsibility**: Primary business logic implementation.
- **Collaborators**: [`ClinicRepository`](../src/main/java/com/gi/clinicservice/repository/ClinicRepository.java) for persistence, [`ClinicMapper`](../src/main/java/com/gi/clinicservice/mapper/ClinicMapper.java) for DTO/entity conversion.
- **Method Details**:
  - `createClinic`: Converts DTO to entity, persists with generated ID, returns DTO copy. No domain validation beyond what the controller ensured.
  - `updateClinic`: Loads existing entity, throws `ResourceNotFoundException` if absent, mutates fields via mapper, saves changes.
  - `activateClinic` / `deactivateClinic`: Fetch entity, set `ClinicStatus` enum, save. No guardrails (e.g., cannot activate expired clinic).
  - `findById`: Returns DTO if entity exists, else 404 via exception.
  - `findAll` / `findActive`: Streams repository results and maps each entity to a DTO.
  - `findNearExpiration`: Computes `LocalDate.now().plusDays(daysBefore)` to determine the cutoff for `ClinicRepository.findByServiceEndDateBefore`.
- **Patterns Observed**: Repeated `repository.findById(...).orElseThrow(...)` could be centralized. Business rules are minimal; service largely proxies repository operations.

### [com.gi.clinicservice.mapper.ClinicMapper](../src/main/java/com/gi/clinicservice/mapper/ClinicMapper.java)

- **Responsibility**: Manual mapping between transport (`ClinicDTO`) and persistence (`Clinic`).
- **Key Methods**:
  - `toEntity`: Builds a new entity from DTO field values (does not set `id`).
  - `toResponse`: Builds a DTO from an entity (also omits `id`).
  - `updateEntity`: Copies DTO fields onto an existing entity instance.
- **Dependencies**: Only the DTO/entity classes.
- **Business Logic**: No transformations or derived values; straight copy. Null checks guard against `NullPointerException` but silently skip updates if DTO is null.
- **Observation**: Because DTO does not carry `id`, returned DTOs also lack identifiers, which limits client usability.

### [com.gi.clinicservice.model.dto.ClinicDTO](../src/main/java/com/gi/clinicservice/model/dto/ClinicDTO.java)

- **Responsibility**: Request/response payload object.
- **Fields**: `name`, `specialty`, `phone`, `address`, `logoUrl`, `status`, `serviceEndDate`.
- **Validation Rules**:
  - `@NotBlank` on `name`, `specialty`, and `address` ensures non-empty text.
  - `@Pattern("\\d{10}")` enforces a 10-digit phone string.
  - `@NotBlank` incorrectly applied to `ClinicStatus` and `LocalDate`, which are not strings; this misconfiguration skips validation at runtime.
- **Business Logic**: Default `status` initialized to `ClinicStatus.ACTIVE`. Does not include `id`, so clients cannot observe generated IDs in responses.

### [com.gi.clinicservice.model.entity.Clinic](../src/main/java/com/gi/clinicservice/model/entity/Clinic.java)

- **Responsibility**: JPA entity mapped to the `clinic` table (defaults from class name).
- **Fields**: `id` (`@Id @GeneratedValue`), `name`, `specialty`, `phone`, `address`, `logoUrl`, `status` (`@Enumerated STRING`), `serviceEndDate`.
- **Dependencies**: Relies on [`ClinicStatus`](../src/main/java/com/gi/clinicservice/model/enums/ClinicStatus.java) enum; uses Lombok `@Data`/`@NoArgsConstructor`/`@AllArgsConstructor` for accessors.
- **Business Logic**: Passive data carrier.

### [com.gi.clinicservice.model.enums.ClinicStatus](../src/main/java/com/gi/clinicservice/model/enums/ClinicStatus.java)

- **Responsibility**: Encodes the allowed service states: `ACTIVE` and `INACTIVE`.
- **Usage**: Controller and service set this value; repository filters active clinics.

### [com.gi.clinicservice.repository.ClinicRepository](../src/main/java/com/gi/clinicservice/repository/ClinicRepository.java)

- **Responsibility**: Persistence gateway built on Spring Data JPA.
- **Custom Queries**:
  - `findByStatus(ClinicStatus status)`: Derived query selecting by enum state.
  - `findByServiceEndDateBefore(LocalDate date)`: Derived query for expiration logic.
- **Business Logic**: Delegated to Spring Data; repository remains declarative.

### [com.gi.clinicservice.exception.ResourceNotFoundException](../src/main/java/com/gi/clinicservice/exception/ResourceNotFoundException.java)

- **Responsibility**: Signals 404 conditions (missing clinics).
- **Usage**: All `findById` paths in `ClinicServiceImpl` throw this when the repository misses.

### [com.gi.clinicservice.exception.BusinessException](../src/main/java/com/gi/clinicservice/exception/BusinessException.java)

- **Responsibility**: Intended for domain validation errors.
- **Usage**: Currently unused, indicating either missing validation paths or dead code.

### [com.gi.clinicservice.exception.GlobalExceptionHandler](../src/main/java/com/gi/clinicservice/exception/GlobalExceptionHandler.java)

- **Responsibility**: Translates custom exceptions to HTTP responses.
- **Handlers**:
  - `handleResourceNotFound`: Returns 404 status with exception message body.
  - `handleBusinessException`: Returns 400 status with exception message body.
- **Observations**: Responses are plain strings; no error codes or localization despite message bundles existing under `resources/messages`.

### [com.gi.clinicservice.ClinicServiceApplicationTests](../src/test/java/com/gi/clinicservice/ClinicServiceApplicationTests.java)

- **Responsibility**: Regression safety net ensuring the Spring context loads.
- **Coverage**: Only `contextLoads()`; no behavioral or integration tests.

## 4. Business Logic Flow

1. **Inbound Validation**: Client sends HTTP request to `ClinicController`. Spring automatically validates `ClinicDTO` using Jakarta validation annotations; invalid payloads never hit the service.
2. **Service Invocation**: Controller calls the matching `ClinicService` method. For writes, DTOs are passed straight through; for reads, only primitive IDs/parameters are forwarded.
3. **Entity Resolution & Mutation**:
   - `ClinicServiceImpl` resolves target entities via `ClinicRepository`. Missing rows trigger `ResourceNotFoundException`.
   - When creating or updating, `ClinicMapper` copies DTO fields to a new or existing `Clinic` entity. No computed fields or side effects currently exist.
4. **Persistence**: Entities are saved through JPA. Derived repository methods (`findByStatus`, `findByServiceEndDateBefore`) perform filtering in the database.
5. **Response Preparation**: Persisted entities are converted back to DTOs (without IDs) and returned to the controller, which wraps them in `ResponseEntity` with HTTP 200.
6. **Error Propagation**: Thrown `ResourceNotFoundException` or `BusinessException` propagate to `GlobalExceptionHandler`, which emits JSON-less string bodies with appropriate status codes.

## 5. Proposed Improvements

1. **DTO Validation Fixes**: Replace `@NotBlank` on `ClinicStatus` and `LocalDate` with `@NotNull`; consider `@FutureOrPresent` for `serviceEndDate` to prevent retroactive contracts.
2. **Response Richness**: Include `id` (and possibly audit timestamps) in response DTOs so clients can reference created clinics.
3. **Business Rules**: Introduce domain checks (e.g., forbid activation if `serviceEndDate` already passed, or enforce unique `name + specialty`). Utilize `BusinessException` for these cases.
4. **Mapper Modernization**: Adopt MapStruct or Lombok builders to remove repetitive setter logic and to support partial updates more safely.
5. **Repository Optimization**: Add pagination for `findAll`/`findActive`, especially once dataset grows, and expose query specs for search.
6. **Error Contract**: Return structured error payloads (code, message, timestamp) leveraging the existing `messages` bundles for localization.
7. **Testing Coverage**: Expand beyond `contextLoads`—add service unit tests (using Mockito) and controller integration tests (MockMvc) to protect business rules.

## 6. Summary

Clinic Service currently offers CRUD endpoints for clinic records backed by Spring Data JPA and a straightforward DTO mapper. The codebase is easy to follow but thin on validation and domain safeguards. Prioritized improvements include fixing DTO validation annotations, enriching responses with identifiers, centralizing repeated repository lookups, enforcing business rules via `BusinessException`, and strengthening automated tests to cover the main use cases.
