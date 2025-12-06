# User Service – Overview

## 1. Introduction

The User Service is a Spring Boot 4 microservice tasked with managing user lifecycle operations (create, read, update, activate, deactivate, list by role) for the CabinetX platform. It exposes HTTP endpoints under `/api/users`, uses Spring Data JPA with an in-memory H2 database by default, and relies on Lombok for boilerplate reduction. Spring Cloud Config and Eureka dependencies are present but disabled in `application.yml` so the application can run standalone during development.

### Architecture Snapshot

- **Entry point:** `com.gi.userservice.UserServiceApplication` enables component scanning and (optionally) service discovery.
- **API layer:** `UserController` handles REST traffic.
- **Service layer:** `UserService` interface plus `UserServiceImpl` implementation contain the domain logic.
- **Persistence layer:** JPA entities (`User`, `DoctorProfile`, `SecretaryProfile`) with repositories for each.
- **DTO layer:** `CreateUserRequest` (input) and `UserDTO` (input/output) transfer data across boundaries.
- **Cross-cutting:** `BusinessException`, `ResourceNotFoundException`, and `GlobalExceptionHandler` provide coarse error handling.

Request flow: `HTTP request → UserController → UserServiceImpl → Spring Data repositories → H2 database → UserDTO response`.

## 2. Class-by-Class Analysis

### 2.1 `com.gi.userservice.UserServiceApplication`
- **Responsibility:** Bootstraps Spring Boot and optionally registers with Eureka (currently disabled by configuration).
- **Key elements:** `@SpringBootApplication`, `@EnableDiscoveryClient`, main method.
- **Dependencies:** None beyond Spring Boot auto-configuration.
- **Notes:** No business logic; failure to configure Eureka correctly is harmless because the client is disabled in `application.yml`.

### 2.2 `com.gi.userservice.config.AppConfig`
- **Responsibility:** Placeholder `@Configuration` class for future bean definitions.
- **Dependencies:** None.
- **Notes:** Currently redundant but harmless.

### 2.3 `com.gi.userservice.controller.UserController`
- **Responsibility:** Defines the REST API under `/api/users`.
- **Endpoints:**
  - `POST /api/users` – create user.
  - `GET /api/users/{id}` – fetch by ID.
  - `PUT /api/users/{id}` – update user details.
  - `POST /api/users/{id}/activate` – mark active.
  - `POST /api/users/{id}/deactivate` – mark inactive.
  - `GET /api/users/role/{role}` – list users by `UserRole`.
- **Dependencies:** Injects `UserService` via Lombok `@RequiredArgsConstructor`.
- **Business flow:** Controllers are thin wrappers that deserialize payloads, call the service, and wrap responses in `ResponseEntity`.
- **Observations:**
  - `@Valid` is used on `CreateUserRequest` and `UserDTO`, but only the former has validation annotations, so update validation does not occur.
  - Despite repository methods for clinic filtering, there is no clinic endpoint in this controller.

### 2.4 `com.gi.userservice.service.UserService`
- **Responsibility:** Defines the contract for user operations (create, find, update, activate, deactivate, list by role).
- **Dependencies:** None, serves purely as an interface.
- **Notes:** Contract no longer mentions clinic-based queries, aligning with the current controller implementation.

### 2.5 `com.gi.userservice.service.impl.UserServiceImpl`
- **Responsibilities:** Implements all business operations, orchestrates repository access, and maps entities to DTOs.
- **Dependencies:** `UserRepository`, `DoctorProfileRepository`, `SecretaryProfileRepository` injected through constructor.
- **Key logic:**
  - **createUser:** Builds a `User` entity from `CreateUserRequest`, saves it, and inserts an empty doctor/secretary profile containing only `userId` for applicable roles. No clinic ID is captured even though DTOs expose that field.
  - **findById:** Retrieves a user or throws `RuntimeException("User not found")`, then maps to `UserDTO`. Clinic information is never populated.
  - **updateUser:** Replaces selectable fields (first name, last name, password, phone, role, active). Login is never updated, and role changes are not synchronized with profile data.
  - **activateUser / deactivateUser:** Toggle the boolean `active` flag, using `BusinessException` to short-circuit redundant changes. Missing IDs still trigger generic `RuntimeException`.
  - **listByRole:** Delegates to `userRepository.findByRole` and maps results to DTOs.
- **Patterns:** Transactional boundaries surround state-changing methods; mapping is centralized in `mapToDTO`.
- **Antipatterns and risks:**
  - Uses raw `RuntimeException` instead of `ResourceNotFoundException`, causing 400 responses instead of 404.
  - Passwords are stored and returned in plaintext.
  - `clinicId` is never set on DTOs even though repositories attempt to expose clinic queries.
  - No duplicate-login protection and no hashing/encoding of credentials.

### 2.6 `com.gi.userservice.repository.UserRepository`
- **Responsibility:** Spring Data repository for `User` entities.
- **Custom methods:** `findByLogin` (unused) and `findByRole`.
- **Notes:** `findByLogin` should be leveraged during creation to detect duplicates before hitting a database constraint.

### 2.7 `com.gi.userservice.repository.DoctorProfileRepository`
- **Responsibility:** CRUD for `DoctorProfile` entities.
- **Custom methods:** `findByUserId`, `findByClinicId`.
- **Issue:** The `DoctorProfile` entity lacks a `clinicId` column, so `findByClinicId` would fail at runtime if invoked. Currently the code never calls it, masking the mismatch.

### 2.8 `com.gi.userservice.repository.SecretaryProfileRepository`
- **Responsibility:** CRUD for `SecretaryProfile` entities.
- **Custom methods:** `findByUserId`, `findByClinicId`.
- **Issue:** Same schema mismatch as `DoctorProfileRepository`; there is no `clinicId` column in `SecretaryProfile`.

### 2.9 `com.gi.userservice.model.entity.User`
- **Fields:** `id`, `firstName`, `lastName`, `login`, `password`, `phoneNumber`, `role`, `active`.
- **Notes:** Password column stores plaintext. There are no auditing fields or relations to profile entities; everything is linked manually through IDs.

### 2.10 `com.gi.userservice.model.entity.DoctorProfile`
- **Fields:** `id`, `userId`, `digitalSignature`.
- **Observations:** Missing `clinicId` even though repositories suggest such a field. No JPA relationship back to `User`.

### 2.11 `com.gi.userservice.model.entity.SecretaryProfile`
- **Fields:** `id`, `userId`.
- **Observations:** Same limitations as `DoctorProfile`: no clinic information, no relationship mapping.

### 2.12 `com.gi.userservice.model.enums.UserRole`
- **Values:** `ADMIN`, `DOCTOR`, `SECRETARY`.
- **Usage:** Stored on `User`, used for request routing and branching logic in the service.

### 2.13 `com.gi.userservice.model.dto.UserDTO`
- **Fields:** Mirrors the `User` entity plus `clinicId`.
- **Usage:** Serves as both update request and response object.
- **Observations:**
  - Contains the password field, so every API response leaks the stored password.
  - Has no Bean Validation annotations, making `@Valid` on the controller ineffective for updates.
  - `clinicId` is never set by the service.

### 2.14 `com.gi.userservice.model.dto.request.CreateUserRequest`
- **Fields:** `firstName`, `lastName`, `login`, `password`, `phoneNumber`, `role`.
- **Validation:** Uses `@NotBlank`, `@NotNull`, and regex-based `@Pattern` constraints.
- **Observations:** No `clinicId` input even though profile repositories hint at clinic associations.

### 2.15 Exceptions and Handler
- **`BusinessException`:** Domain-level runtime exception used when activating/deactivating an already-active state.
- **`ResourceNotFoundException`:** Declared but never thrown; not integrated into the service layer.
- **`GlobalExceptionHandler`:**
  - Maps `ResourceNotFoundException` to 404 (declared twice, indicating copy/paste).
  - Maps `RuntimeException` and `BusinessException` to 400.
  - Maps `MethodArgumentNotValidException` to a simple `Map<String,String>` body.
  - No consistent error envelope, and duplicate handler methods are present.

### 2.16 `com.gi.userservice.UserServiceApplicationTests`
- **Responsibility:** Smoke test ensuring the Spring context loads.
- **Annotations:** `@SpringBootTest` with test-only property overrides to disable Config Server and Eureka.
- **Coverage:** No business tests; does not exercise controller or service logic.

### 2.17 Configuration & Resources
- **`application.yml`:**
  - Activates the `dev` profile.
  - Configures an H2 in-memory datasource with Hibernate `ddl-auto: update` and SQL logging enabled.
  - Disables Spring Cloud Config and Eureka clients.
  - Exposes Actuator `health` and `info` endpoints and runs on port `8085`.
- **Message bundles (`messages.properties`, `messages_fr.properties`):** placeholders; not wired into a `MessageSource`.

## 3. Business Logic Flow

1. **Create user (`POST /api/users`):**
   - Controller validates `CreateUserRequest` via Bean Validation.
   - Service creates a `User`, persists it, then inserts a role-specific profile containing only `userId`.
   - DTO mapping mirrors entity data; password is included in the response.

2. **Get user (`GET /api/users/{id}`):**
   - Service loads the user or throws `RuntimeException("User not found")` (mapped to HTTP 400).
   - No additional data enrichment occurs; `clinicId` remains `null`.

3. **Update user (`PUT /api/users/{id}`):**
   - Incoming payload is a `UserDTO`; no validation occurs.
   - Service updates mutable fields and saves the entity.
   - Clinic/profile data is ignored, and login remains immutable.

4. **Activate/deactivate (`POST /api/users/{id}/activate` or `/deactivate`):**
   - Service fetches the user, checks the current `active` flag, and toggles when appropriate.
   - Redundant operations raise `BusinessException`, returning `400 Bad Request`.

5. **List by role (`GET /api/users/role/{role}`):**
   - Controller converts the path variable to `UserRole`.
   - Service queries by role and returns DTOs.
   - No pagination, filtering, or clinic enrichment.

## 4. Proposed Improvements

1. **Exception strategy:**
   - Throw `ResourceNotFoundException` for missing entities to produce proper 404 responses.
   - Use `BusinessException` (or a new `ValidationException`) for duplicate login, invalid role switches, etc., and add consistent error payloads in the handler.

2. **Security:**
   - Hash passwords before storing (e.g., `BCryptPasswordEncoder`).
   - Remove password from `UserDTO` responses; introduce separate request/response DTOs to avoid leaking credentials.

3. **Clinic/Profile modelling:**
   - Add `clinicId` (and other required fields) to `DoctorProfile` and `SecretaryProfile`, or remove the unused repository methods until the feature is ready.
   - Replace manual `userId` fields with JPA associations to ensure referential integrity.

4. **Validation & DTO design:**
   - Add validation annotations to `UserDTO` or introduce an `UpdateUserRequest` with explicit constraints.
   - Validate role transitions (e.g., require clinic information when creating a doctor/secretary) and enforce unique logins before persistence.

5. **Error handling & responses:**
   - Deduplicate handlers in `GlobalExceptionHandler` and standardize error bodies (timestamp, status, message, path).
   - Ensure `MethodArgumentNotValidException` returns a structured object rather than a raw map.

6. **Mapping & service cohesion:**
   - Extract a mapper (manual utility or MapStruct) so the service implementation focuses on business rules instead of field copying.
   - Consolidate repeated clinic-enrichment logic once clinic data is actually available.

7. **Testing:**
   - Add unit tests for `UserServiceImpl` covering happy paths and validation failures.
   - Add MVC tests for `UserController` to verify HTTP status codes and error responses.

8. **Configuration & tooling:**
   - Replace `ddl-auto: update` with schema migrations (Flyway/Liquibase) before production.
   - Consider adding Docker support or document how to set real database credentials.

## 5. Summary

The current User Service provides the basic REST endpoints required to create, update, activate/deactivate, and list users by role. Controllers are thin and the codebase is small, but several gaps remain: passwords are exposed in every response, clinic associations are not actually implemented, runtime exceptions lead to incorrect HTTP status codes, and validation is incomplete for updates. Addressing the improvements listed above—especially exception semantics, password handling, and schema alignment for clinic data—will bring the service closer to production readiness while making it easier to maintain and extend.
