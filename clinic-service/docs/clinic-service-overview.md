# Clinic Service Overview

## 1. Introduction
Clinic Service is a Spring Boot microservice that exposes CRUD-style REST endpoints to manage clinic definitions. It follows a layered architecture built on top of Spring Web MVC, Spring Data JPA, and H2/MySQL datasources. Requests flow through a REST controller, a service layer that hosts business rules, a mapper that converts DTOs to entities, and a repository backed by JPA entities.

## 2. Class-by-Class Analysis

### [com.gi.clinicservice.ClinicServiceApplication](src/main/java/com/gi/clinicservice/ClinicServiceApplication.java)
- **Responsibility**: Spring Boot entry point bootstrapping component scanning and auto-configuration.
- **Key Methods**: `main` delegates to `SpringApplication.run`.
- **Dependencies**: Spring Boot auto-configures web, JPA, validation, and Cloud clients based on the dependencies declared in `pom.xml`.
- **Business Logic**: None; serves as bootstrap.

### [com.gi.clinicservice.controller.ClinicController](src/main/java/com/gi/clinicservice/controller/ClinicController.java)
- **Responsibility**: Defines REST endpoints under `/api/clinics`.
- **Key Endpoints**:
  - `POST /api/clinics` → `createClinic`
  - `PUT /api/clinics/{id}` → `updateClinic`
  - `PATCH /api/clinics/{id}/activate` → `activateClinic`
  - `PATCH /api/clinics/{id}/deactivate` → `deactivateClinic`
  - `GET /api/clinics/{id}` → `findById`
  - `GET /api/clinics` → `findAll`
  - `GET /api/clinics/active` → `findActive`
  - `GET /api/clinics/near-expiration` → `findNearExpiration`
- **Dependencies**: Injects [`ClinicService`](src/main/java/com/gi/clinicservice/service/ClinicService.java) to delegate work, uses Jakarta Bean Validation for request checking, wraps responses in `ResponseEntity`.
- **Business Logic Flow**: Stateless, simply relays validated DTOs and parameters to the service layer.

### [com.gi.clinicservice.service.ClinicService](src/main/java/com/gi/clinicservice/service/ClinicService.java)
- **Responsibility**: Contract describing business operations for clinics.
- **Key Methods**: `createClinic`, `updateClinic`, activation/deactivation, fetches, and `findNearExpiration`.
- **Dependencies**: Implementations must orchestrate repositories and mappers.
- **Business Logic**: Interface only; ensures consistent API for controllers.

### [com.gi.clinicservice.service.impl.ClinicServiceImpl](src/main/java/com/gi/clinicservice/service/impl/ClinicServiceImpl.java)
- **Responsibility**: Implements business logic defined in `ClinicService`.
- **Key Attributes**: `ClinicRepository repository`, `ClinicMapper mapper`.
- **Key Methods**:
  - Persistence wrappers (create/update/find) with `ResourceNotFoundException` handling.
  - Activation logic forcing `ClinicStatus.ACTIVE`.
  - Near-expiration query by computing `LocalDate.now().plusDays(daysBefore)`.
- **Dependencies**:
  - [`ClinicRepository`](src/main/java/com/gi/clinicservice/repository/ClinicRepository.java) for data access.
  - [`ClinicMapper`](src/main/java/com/gi/clinicservice/mapper/ClinicMapper.java) for DTO ↔ entity conversion.
  - [`ClinicStatus`](src/main/java/com/gi/clinicservice/model/enums/ClinicStatus.java) for enum transitions.
- **Business Logic Flow**: Validates existence via repository, transforms DTOs, persists, and returns DTO responses. Repeated `orElseThrow` ensures 404 semantics.
- **Patterns**: Typical Service Layer + Mapper. Potential duplication of `repository.findById` logic suggests extraction to helper.

### [com.gi.clinicservice.mapper.ClinicMapper](src/main/java/com/gi/clinicservice/mapper/ClinicMapper.java)
- **Responsibility**: Manual mapping between DTOs and entities.
- **Key Methods**:
  - `toEntity(ClinicDTO)`
  - `toResponse(Clinic)`
  - `updateEntity(ClinicDTO, Clinic)`
- **Dependencies**: Works on [`ClinicDTO`](src/main/java/com/gi/clinicservice/model/dto/ClinicDTO.java) and [`Clinic`](src/main/java/com/gi/clinicservice/model/entity/Clinic.java).
- **Business Logic**: Field-by-field copying, including enums and dates. No null handling beyond short-circuit returns; does not populate entity ID.

### [com.gi.clinicservice.repository.ClinicRepository](src/main/java/com/gi/clinicservice/repository/ClinicRepository.java)
- **Responsibility**: Data access layer using Spring Data JPA.
- **Key Methods**: `findByStatus`, `findByServiceEndDateBefore`.
- **Dependencies**: Extends `JpaRepository<Clinic, Long>`, enabling CRUD operations.
- **Business Logic**: None beyond derived query methods; handles filtering by status/date.

### [com.gi.clinicservice.model.entity.Clinic](src/main/java/com/gi/clinicservice/model/entity/Clinic.java)
- **Responsibility**: JPA entity describing persisted clinic data.
- **Key Attributes**: `id`, `name`, `specialty`, `phone`, `address`, `logoUrl`, `status`, `serviceEndDate`.
- **Dependencies**: Uses [`ClinicStatus`](src/main/java/com/gi/clinicservice/model/enums/ClinicStatus.java) enum, Jakarta Persistence annotations, Lombok for boilerplate.
- **Business Logic**: Pure data model; no behaviors.

### [com.gi.clinicservice.model.dto.ClinicDTO](src/main/java/com/gi/clinicservice/model/dto/ClinicDTO.java)
- **Responsibility**: API payload for Clinic operations.
- **Key Attributes**: Mirrors entity fields (without `id`).
- **Validation**:
  - `@NotBlank` on `name`, `specialty`, `address`.
  - `@Pattern` for 10-digit phone.
  - Incorrect use of `@NotBlank` on `ClinicStatus` and `LocalDate` (should be `@NotNull`), causing validator misconfiguration.
- **Business Logic**: Default `status` to `ACTIVE` but no default for dates.

### [com.gi.clinicservice.model.enums.ClinicStatus](src/main/java/com/gi/clinicservice/model/enums/ClinicStatus.java)
- **Responsibility**: Enumerates possible states (`ACTIVE`, `INACTIVE`).
- **Business Logic**: None; used for state transitions in service/controller.

### [com.gi.clinicservice.exception.ResourceNotFoundException](src/main/java/com/gi/clinicservice/exception/ResourceNotFoundException.java)
- **Responsibility**: Signals missing resources (HTTP 404).
- **Business Logic**: Stores message used by handler and controller responses.

### [com.gi.clinicservice.exception.BusinessException](src/main/java/com/gi/clinicservice/exception/BusinessException.java)
- **Responsibility**: Placeholder for future domain validation errors.
- **Usage**: Currently unused anywhere else—dead code.

### [com.gi.clinicservice.exception.GlobalExceptionHandler](src/main/java/com/gi/clinicservice/exception/GlobalExceptionHandler.java)
- **Responsibility**: Maps exceptions to HTTP responses.
- **Key Methods**:
  - `handleResourceNotFound` → 404 with message.
  - `handleBusinessException` → 400 with message.
- **Dependencies**: Spring’s `@RestControllerAdvice`, both custom exceptions.
- **Business Logic**: No localization/internationalization, returns raw message strings.

### [com.gi.clinicservice.ClinicServiceApplicationTests](src/test/java/com/gi/clinicservice/ClinicServiceApplicationTests.java)
- **Responsibility**: Smoke test to ensure Spring context loads.
- **Business Logic**: None; default `contextLoads` test.

## 3. Business Logic Flow
1. **Request Entry**: HTTP requests hit [`ClinicController`](src/main/java/com/gi/clinicservice/controller/ClinicController.java), where payloads are validated via Jakarta annotations.
2. **Service Delegation**: Controller delegates to [`ClinicService`](src/main/java/com/gi/clinicservice/service/ClinicService.java) via its implementation [`ClinicServiceImpl`](src/main/java/com/gi/clinicservice/service/impl/ClinicServiceImpl.java).
3. **Entity Handling**:
   - For create/update: DTOs converted to entities using [`ClinicMapper`](src/main/java/com/gi/clinicservice/mapper/ClinicMapper.java).
   - For reads: Entities fetched from [`ClinicRepository`](src/main/java/com/gi/clinicservice/repository/ClinicRepository.java).
4. **Persistence**: Repository performs CRUD through Spring Data JPA against H2/MySQL depending on profile.
5. **Response Assembly**: Updated entities passed back through the mapper into DTOs and returned to the controller, which wraps them in `ResponseEntity`.
6. **Exception Flow**: Missing entities trigger `ResourceNotFoundException`, which the [`GlobalExceptionHandler`](src/main/java/com/gi/clinicservice/exception/GlobalExceptionHandler.java) converts to 404 responses; future domain errors can throw `BusinessException` for 400 responses.

## 4. Proposed Improvements
1. **Validation Accuracy**: Replace `@NotBlank` on non-String fields in [`ClinicDTO`](src/main/java/com/gi/clinicservice/model/dto/ClinicDTO.java) with `@NotNull` and add constraints for dates (`@FutureOrPresent`) to ensure correct schema checking.
2. **DTO Completeness**: Include `id` in `ClinicDTO` or create separate request/response DTOs so update responses carry identifiers.
3. **Mapper Enhancements**: Consider MapStruct or record-based constructors to reduce boilerplate and ensure immutability.
4. **Service Refactoring**: Extract repeated `findById` logic in [`ClinicServiceImpl`](src/main/java/com/gi/clinicservice/service/impl/ClinicServiceImpl.java) into a private helper. Add business validation (e.g., cannot activate expired clinic).
5. **Exception Localization**: Utilize message bundles (already present) for error messages, injecting `MessageSource` into the service or handler.
6. **Testing Coverage**: Add unit tests for service logic and controller (MockMvc) to cover success/error flows.
7. **API Documentation**: Introduce OpenAPI/Swagger for automatic endpoint documentation.

## 5. Summary
Clinic Service currently exposes CRUD operations for clinic records, persisting them via Spring Data JPA and returning DTOs through a controller-service-repository stack. Key improvements needed:
- Correct validation annotations and enrich business rules.
- Provide identifiers and richer responses in DTOs.
- Increase test coverage and automate documentation.
- Localize error messaging and enforce consistent exception usage.