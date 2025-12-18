# Medical Record Service Overview

## Introduction

The [medical-record-service](pom.xml) is a Spring Boot microservice dedicated to persisting and retrieving longitudinal medical records, their supporting documents, and the consultation history of a patient. It exposes a REST API for clinical applications, stores structured data through JPA entities, and enriches each record with remote consultation details fetched via an OpenFeign client.

Architecturally, the service follows a layered approach:

- **Entrypoint**: [`com.gi.medicalrecordservice.MedicalRecordServiceApplication`](src/main/java/com/gi/medicalrecordservice/MedicalRecordServiceApplication.java) boots the application and enables Feign clients.
- **Web layer**: [`com.gi.medicalrecordservice.controller.MedicalRecordController`](src/main/java/com/gi/medicalrecordservice/controller/MedicalRecordController.java) exposes HTTP endpoints.
- **Service layer**: [`com.gi.medicalrecordservice.service.impl.MedicalRecordServiceImpl`](src/main/java/com/gi/medicalrecordservice/service/impl/MedicalRecordServiceImpl.java) orchestrates business logic with transactional boundaries.
- **Integration layer**: [`com.gi.medicalrecordservice.client.ConsultationServiceClient`](src/main/java/com/gi/medicalrecordservice/client/ConsultationServiceClient.java) pulls consultation history from the external consultation-service.
- **Persistence layer**: [`com.gi.medicalrecordservice.repository.MedicalRecordRepository`](src/main/java/com/gi/medicalrecordservice/repository/MedicalRecordRepository.java) and [`com.gi.medicalrecordservice.repository.MedicalDocumentRepository`](src/main/java/com/gi/medicalrecordservice/repository/MedicalDocumentRepository.java) manage database access for aggregate roots.
- **Mapping layer**: [`com.gi.medicalrecordservice.mapper.MedicalRecordMapper`](src/main/java/com/gi/medicalrecordservice/mapper/MedicalRecordMapper.java) translates between entities and DTOs.
- **Domain layer**: Entity, enum, and DTO packages hold the data model.

Configuration is minimal via [src/main/resources/application.yml](src/main/resources/application.yml); demo data is seeded through [`com.gi.medicalrecordservice.config.MedicalRecordInitializer`](src/main/java/com/gi/medicalrecordservice/config/MedicalRecordInitializer.java).

## Class-by-Class Analysis

### [`com.gi.medicalrecordservice.MedicalRecordServiceApplication`](src/main/java/com/gi/medicalrecordservice/MedicalRecordServiceApplication.java)

- **Responsibility**: Main entry point; enables component scanning and Feign clients targeting `com.gi.medicalrecordservice.client`.
- **Key methods**: `main(String[] args)` runs `SpringApplication`.
- **Dependencies**: Spring Boot auto-configuration, Feign.
- **Business logic**: None; purely bootstrap.
- **Patterns**: Standard Spring Boot application pattern.

### [`com.gi.medicalrecordservice.config.AppConfig`](src/main/java/com/gi/medicalrecordservice/config/AppConfig.java)

- **Responsibility**: Placeholder configuration class intended for shared bean definitions.
- **Current state**: Empty; presents an opportunity to centralize common beans (e.g., `MessageSource`).
- **Patterns**: Configuration class pattern awaiting implementation.

### [`com.gi.medicalrecordservice.config.MedicalRecordInitializer`](src/main/java/com/gi/medicalrecordservice/config/MedicalRecordInitializer.java)

- **Responsibility**: Seeds demo medical records and documents at startup.
- **Key methods**: `seedData()` annotated with `@PostConstruct` and `@Transactional`.
- **Dependencies**: [`MedicalRecordRepository`](src/main/java/com/gi/medicalrecordservice/repository/MedicalRecordRepository.java), domain entities, [`com.gi.medicalrecordservice.model.enums.DocumentType`](src/main/java/com/gi/medicalrecordservice/model/enums/DocumentType.java).
- **Business logic**: Checks repository count, then creates sample records/documents with generated URLs and timestamps.
- **Patterns**: Data seeding pattern; uses builder pattern on entities.

### [`com.gi.medicalrecordservice.util.CommonUtils`](src/main/java/com/gi/medicalrecordservice/util/CommonUtils.java)

- **Responsibility**: Intended holder for cross-cutting helpers.
- **Current state**: Empty placeholder.
- **Improvement opportunity**: Remove until needed or populate with actual utilities to avoid dead code.

### [`com.gi.medicalrecordservice.client.ConsultationServiceClient`](src/main/java/com/gi/medicalrecordservice/client/ConsultationServiceClient.java)

- **Responsibility**: OpenFeign client to the consultation-service.
- **Key methods**: `getConsultationsByPatient(Long patientId)` calling `/api/consultations/patients/{patientId}/historique`.
- **Dependencies**: Feign, `ConsultationInfo` DTO.
- **Business logic**: Declarative HTTP integration; relies on service layer for error handling.
- **Patterns**: API Gateway pattern via Feign.

### [`com.gi.medicalrecordservice.controller.MedicalRecordController`](src/main/java/com/gi/medicalrecordservice/controller/MedicalRecordController.java)

- **Responsibility**: REST facade for record lifecycle, document management, and consultation history retrieval.
- **Endpoints**:
  - `POST /api/records/patients/{patientId}` → open or create record.
  - `PUT /api/records/{recordId}` → update record metadata.
  - `GET /api/records/patients/{patientId}` → fetch complete record with documents and consultations.
  - `POST /api/records/{recordId}/documents` (multipart) → attach document.
  - `DELETE /api/records/documents/{documentId}` → remove document.
  - `GET /api/records/patients/{patientId}/consultations` → proxy consultation history.
- **Dependencies**: [`MedicalRecordService`](src/main/java/com/gi/medicalrecordservice/service/MedicalRecordService.java); uses `@Valid` on `MedicalRecordDTO`.
- **Business logic**: Thin delegation; HTTP concerns (status codes, content type) handled at controller level.
- **Patterns / Anti-patterns**: REST controller pattern; uses response DTO as request payload (anti-pattern for updates).

### [`com.gi.medicalrecordservice.service.MedicalRecordService`](src/main/java/com/gi/medicalrecordservice/service/MedicalRecordService.java)

- **Responsibility**: Defines service contract.
- **Methods**: `openOrCreateRecord`, `updateRecord`, `getCompleteRecord`, `addDocument`, `deleteDocument`, `getConsultationHistory`.
- **Dependencies**: DTOs and enums.
- **Pattern**: Interface segregation enabling alternative implementations/testing.

### [`com.gi.medicalrecordservice.service.impl.MedicalRecordServiceImpl`](src/main/java/com/gi/medicalrecordservice/service/impl/MedicalRecordServiceImpl.java)

- **Responsibility**: Core business logic with transactional semantics.
- **Key attributes**: Repositories, Feign client, mapper.
- **Key methods**:
  - `openOrCreateRecord`: Idempotently fetches or creates `MedicalRecord`.
  - `updateRecord`: Mutates record fields and persists updates.
  - `getCompleteRecord`: Loads record by patient ID and enriches it.
  - `addDocument`: Creates `MedicalDocument` from `MultipartFile`, persisting binary content and returning DTO.
  - `deleteDocument`: Validates existence then deletes.
  - `getConsultationHistory`: Delegates to Feign client with graceful degradation (returns empty list on failure).
  - Helpers `enrichRecord` and `loadRecord`.
- **Business logic flow**: Compose data by combining record entity, associated documents, and remote consultations before mapping to DTO.
- **Patterns / Anti-patterns**: Transaction Script; manual enrichment ensures single DTO; anti-pattern includes mixing persistence concerns (e.g., storing binary blobs) with service layer and swallowing Feign errors without user feedback.

### [`com.gi.medicalrecordservice.mapper.MedicalRecordMapper`](src/main/java/com/gi/medicalrecordservice/mapper/MedicalRecordMapper.java)

- **Responsibility**: Converts JPA entities and remote data into DTOs.
- **Methods**: `toDto`, `toDocumentDtoList`, `toDocumentDto`.
- **Dependencies**: DTO classes and entity classes.
- **Business logic**: Aggregates `MedicalRecord`, `MedicalDocument` list, and `ConsultationInfo` list into a single `MedicalRecordDTO`.
- **Patterns**: Mapper pattern using Lombok builders; handles `null`/empty lists defensively.

### [`com.gi.medicalrecordservice.repository.MedicalRecordRepository`](src/main/java/com/gi/medicalrecordservice/repository/MedicalRecordRepository.java)

- **Responsibility**: CRUD access to `MedicalRecord`.
- **Key methods**: Inherited JPA operations plus custom `findByPatientId(Long patientId)`.
- **Dependencies**: Spring Data JPA.
- **Patterns**: Repository pattern with derived query method.

### [`com.gi.medicalrecordservice.repository.MedicalDocumentRepository`](src/main/java/com/gi/medicalrecordservice/repository/MedicalDocumentRepository.java)

- **Responsibility**: CRUD for `MedicalDocument`.
- **Key methods**: `findByMedicalRecordId(Long medicalRecordId)` plus inherited JPA.
- **Business logic**: Supports retrieval of document collections per record.
- **Patterns**: Repository with derived query.

### [`com.gi.medicalrecordservice.model.entity.MedicalRecord`](src/main/java/com/gi/medicalrecordservice/model/entity/MedicalRecord.java)

- **Responsibility**: Aggregate root storing patient-level metadata.
- **Fields**: `id`, `patientId`, `medicalHistory`, `allergies`, `treatments`, `habits`, `creationDate`, `lastUpdate`, `documents`.
- **Lifecycle hooks**: `@PrePersist` sets `creationDate`/`lastUpdate`; `@PreUpdate` refreshes timestamp.
- **Associations**: `@OneToMany` with `MedicalDocument` (cascade all, orphan removal, lazy fetch).
- **Business logic**: `addDocument` helper maintains bidirectional consistency.
- **Patterns**: JPA entity with Lombok builder and default list initialization.

### [`com.gi.medicalrecordservice.model.entity.MedicalDocument`](src/main/java/com/gi/medicalrecordservice/model/entity/MedicalDocument.java)

- **Responsibility**: Stores binary artifacts tied to a record.
- **Fields**: `id`, `medicalRecord`, `fileName`, `fileType`, `fileUrl`, `documentType`, `content` (`@Lob`), `additionDate`.
- **Lifecycle**: `@PrePersist` initializes `additionDate`.
- **Business logic**: Strict non-null constraints for `fileName`, `documentType`, `content`.
- **Patterns**: JPA entity with Lombok; persists binary content in the database.

### [`com.gi.medicalrecordservice.model.enums.DocumentType`](src/main/java/com/gi/medicalrecordservice/model/enums/DocumentType.java)

- **Responsibility**: Enumerates supported document categories (`ANALYSIS`, `XRAY`, `CHECKUP`, `PRESCRIPTION`, `OTHER`).
- **Usage**: Document creation and DTO serialization; stored as `EnumType.STRING`.
- **Patterns**: Simple enum for domain vocabulary.

### [`com.gi.medicalrecordservice.model.enums.ConsultationType`](src/main/java/com/gi/medicalrecordservice/model/enums/ConsultationType.java)

- **Responsibility**: Defines consultation classifications (`CONSULTATION`, `FOLLOWUP`).
- **Usage**: Embedded in `ConsultationInfo` from remote service.

### [`com.gi.medicalrecordservice.model.dto.response.MedicalRecordDTO`](src/main/java/com/gi/medicalrecordservice/model/dto/response/MedicalRecordDTO.java)

- **Responsibility**: API response payload for medical records.
- **Fields**: Mirrors `MedicalRecord` plus `List<MedicalDocumentDTO>` and `List<ConsultationInfo>`.
- **Usage**: Returned by controller methods; also reused as request body for updates (anti-pattern because of missing validation granularity).

### [`com.gi.medicalrecordservice.model.dto.response.MedicalDocumentDTO`](src/main/java/com/gi/medicalrecordservice/model/dto/response/MedicalDocumentDTO.java)

- **Responsibility**: API projection of `MedicalDocument`.
- **Fields**: Basic metadata (omits binary content).
- **Usage**: Response to document operations.

### [`com.gi.medicalrecordservice.model.dto.response.ConsultationInfo`](src/main/java/com/gi/medicalrecordservice/model/dto/response/ConsultationInfo.java)

- **Responsibility**: DTO reflecting consultation records obtained from the consultation-service.
- **Fields**: `id`, `consultationDate`, `type`, `diagnosis`, `treatment`.
- **Usage**: Combined into record responses and returned by consultation history endpoint.

### [`com.gi.medicalrecordservice.exception.BusinessException`](src/main/java/com/gi/medicalrecordservice/exception/BusinessException.java)

- **Responsibility**: Placeholder domain exception.
- **Usage**: Currently unused; indicates planned richer error semantics.
- **Improvement**: Either remove or integrate into service logic.

### [`com.gi.medicalrecordservice.exception.ResourceNotFoundException`](src/main/java/com/gi/medicalrecordservice/exception/ResourceNotFoundException.java)

- **Responsibility**: Simple runtime exception for missing resources.
- **Usage**: Not referenced; controller/service rely on `ResponseStatusException` instead.
- **Anti-pattern**: Dead code; prefer consistent exception strategy.

### [`com.gi.medicalrecordservice.exception.GlobalExceptionHandler`](src/main/java/com/gi/medicalrecordservice/exception/GlobalExceptionHandler.java)

- **Responsibility**: Converts exceptions into HTTP responses.
- **Handlers**:
  - `ResponseStatusException` → propagate status/reason.
  - Generic `Exception` → 500 with generic message.
- **Dependencies**: Spring’s `@RestControllerAdvice`.
- **Patterns**: Centralized exception handling; limited coverage (e.g., does not translate custom exceptions yet).

### [`com.gi.medicalrecordservice.MedicalRecordServiceApplicationTests`](src/test/java/com/gi/medicalrecordservice/MedicalRecordServiceApplicationTests.java)

- **Responsibility**: Smoke test ensuring Spring context loads.
- **Coverage**: Minimal; no assertions beyond context initialization.

### [`com.gi.medicalrecordservice.controller.MedicalRecordControllerTest`](src/test/java/com/gi/medicalrecordservice/controller/MedicalRecordControllerTest.java)

- **Responsibility**: Placeholder test class.
- **Current state**: Contains only `contextLoads` comment; no actual tests.

### [`com.gi.medicalrecordservice.repository.MedicalRecordRepositoryTest`](src/test/java/com/gi/medicalrecordservice/repository/MedicalRecordRepositoryTest.java)

- **Responsibility**: Placeholder for repository tests; currently empty.

### [`com.gi.medicalrecordservice.service.MedicalRecordServiceTest`](src/test/java/com/gi/medicalrecordservice/service/MedicalRecordServiceTest.java)

- **Responsibility**: Placeholder; lacks mock/service tests.

### [`com.gi.medicalrecordservice.integration.IntegrationTest`](src/test/java/com/gi/medicalrecordservice/integration/IntegrationTest.java)

- **Responsibility**: Placeholder for end-to-end tests; currently empty.

## Business Logic Flow

1. **Record creation/opening**:
   - `POST /api/records/patients/{patientId}` hits [`MedicalRecordController`](src/main/java/com/gi/medicalrecordservice/controller/MedicalRecordController.java).
   - Controller delegates to [`MedicalRecordServiceImpl.openOrCreateRecord`](src/main/java/com/gi/medicalrecordservice/service/impl/MedicalRecordServiceImpl.java), which queries [`MedicalRecordRepository.findByPatientId`](src/main/java/com/gi/medicalrecordservice/repository/MedicalRecordRepository.java).
   - If absent, a new [`MedicalRecord`](src/main/java/com/gi/medicalrecordservice/model/entity/MedicalRecord.java) is built, persisted, and enriched via `enrichRecord`, which fetches child documents and remote consultations before mapping to `MedicalRecordDTO`.
   - Response returns `201 Created` with aggregated JSON.

2. **Record update**:
   - `PUT /api/records/{recordId}` receives a `MedicalRecordDTO`.
   - Service uses `loadRecord` to fetch the entity, mutates tracked fields (medical history, allergies, treatments, habits), saves, then returns `MedicalRecordDTO`.

3. **Document management**:
   - `POST /api/records/{recordId}/documents` accepts multipart data.
   - Service builds a [`MedicalDocument`](src/main/java/com/gi/medicalrecordservice/model/entity/MedicalDocument.java) with metadata and `file.getBytes()`, persists it via [`MedicalDocumentRepository`](src/main/java/com/gi/medicalrecordservice/repository/MedicalDocumentRepository.java), and returns a `MedicalDocumentDTO`.
   - `DELETE /api/records/documents/{documentId}` loads the document and deletes it.

4. **Record retrieval**:
   - `GET /api/records/patients/{patientId}` ensures a record exists via repository, collects associated documents, issues `ConsultationServiceClient.getConsultationsByPatient`, and composes the response.

5. **Consultation history endpoint**:
   - `GET /api/records/patients/{patientId}/consultations` directly proxies the Feign call, logging warnings and returning an empty list if the remote call fails.

6. **Error handling**:
   - Service methods throw `ResponseStatusException` for 404 or 400 scenarios (e.g., missing record/document, unreadable file).
   - [`GlobalExceptionHandler`](src/main/java/com/gi/medicalrecordservice/exception/GlobalExceptionHandler.java) harmonizes responses.

## Proposed Improvements

1. **Introduce dedicated request DTOs with validation**: Replacing the reuse of `MedicalRecordDTO` for updates with a `MedicalRecordUpdateRequest` containing `@NotBlank`/`@Size` constraints would prevent unintended field updates and improve input validation clarity.
2. **Externalize document storage**: Persisting `byte[]` content directly in the relational database can degrade performance. Storing files in object storage (e.g., S3, Azure Blob) and keeping only metadata/URLs in [`MedicalDocument`](src/main/java/com/gi/medicalrecordservice/model/entity/MedicalDocument.java) would reduce database load and simplify streaming large files.
3. **Harden Feign integration**: Wrapping [`ConsultationServiceClient`](src/main/java/com/gi/medicalrecordservice/client/ConsultationServiceClient.java) calls with Resilience4j circuit breakers/retries and surfacing partial-failure indicators in responses would provide better observability than silently returning an empty list.
4. **Unify exception strategy**: Replace ad-hoc `ResponseStatusException` usage with domain-specific exceptions (e.g., reusing `ResourceNotFoundException`) and expand [`GlobalExceptionHandler`](src/main/java/com/gi/medicalrecordservice/exception/GlobalExceptionHandler.java) to map them to localized error payloads (leveraging [messages/messages.properties](src/main/resources/messages/messages.properties)).
5. **Strengthen automated testing**: Populate the placeholder test classes with unit tests (service/repository) and integration tests that cover record CRUD, document upload limits, and Feign fallbacks to prevent regressions.
6. **Remove or implement placeholders**: Classes like [`CommonUtils`](src/main/java/com/gi/medicalrecordservice/util/CommonUtils.java) and unused exceptions should either be implemented or deleted to reduce cognitive overhead.

## Summary

The medical-record-service currently provides CRUD operations for patient records, document attachment/deletion, and consultation history aggregation by layering REST controllers, a transactional service, repositories, and a Feign client. Lombok-powered entities/DTOs, plus a mapper, simplify data transformations, while demo data seeding enables quick smoke testing.

**Key improvements needed:**

- Introduce purpose-built request DTOs with validation for write operations.
- Move binary document storage out of the relational database and add streaming/download endpoints.
- Add resiliency, logging, and error transparency around the Feign client.
- Consolidate exception handling and remove unused scaffolding classes.
- Implement meaningful unit/integration tests to cover business-critical flows.
