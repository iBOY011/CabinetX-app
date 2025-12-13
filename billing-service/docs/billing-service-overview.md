# Billing Service Overview

## Introduction

The billing service is a Spring Boot 4.0 application that manages invoices for medical consultations. It exposes REST endpoints, persists invoices through Spring Data JPA, and stores data in an in-memory H2 database configured in application.yml. The runtime stack combines Spring Web MVC, Spring Data JPA, and Eureka client dependencies, but most integrations (patient lookup, consultation lookup, PDF export) are currently mocked or stubbed.

## Architecture Overview

The service follows a layered architecture:

- **API Layer** – `com.gi.billingservice.controller.BillingController` routes HTTP requests.
- **Service Layer** – `com.gi.billingservice.service.BillingService` defines use cases implemented by `com.gi.billingservice.service.impl.BillingServiceImpl`.
- **Persistence Layer** – `com.gi.billingservice.repository.InvoiceRepository` manages the `com.gi.billingservice.model.entity.Invoice` entity.
- **DTO & Mapping Layer** – DTOs under `model.dto.*` are assembled via `com.gi.billingservice.mapper.InvoiceMapper`.
- **Cross-cutting** – Exception handling via `com.gi.billingservice.exception.GlobalExceptionHandler` plus placeholder clients and configuration stubs.

## Class-by-Class Analysis

### com.gi.billingservice.BillingServiceApplication

- **Responsibility**: Spring Boot entry point.
- **Key Methods**: `main` boots the application context.
- **Dependencies**: Implicitly loads all Spring components via component scanning.
- **Notes**: No customizations; relies on default auto-configuration.

### com.gi.billingservice.config.AppConfig

- **Responsibility**: Placeholder for bean definitions.
- **Current State**: Empty `@Configuration` class; no beans defined.
- **Improvement**: Remove until needed or populate with reusable beans (e.g., ModelMapper, MessageSource).

### com.gi.billingservice.config.SecurityConfig

- **Responsibility**: Placeholder for security setup.
- **Current State**: Empty; no Spring Security dependency configured.
- **Improvement**: Either add Spring Security and define HTTP security or delete the class.

### com.gi.billingservice.controller.BillingController

- **Responsibility**: REST API for invoice lifecycle.
- **Endpoints**: POST `/invoices`, PUT `/invoices/{id}/payment`, PUT `/invoices/{id}/cancel`, GET `/invoices/{id}`, GET `/patients/{patientId}/invoices`, GET `/invoices/{id}/pdf`.
- **Dependencies**: Injects `BillingService`.
- **Business Logic**: Delegates to service; no validation annotations, so relies on controller method signatures and Spring conversion for parameters.
- **Pattern Notes**: Thin controller pattern; consistent ResponseEntity usage.

### com.gi.billingservice.service.BillingService

- **Responsibility**: Defines invoice-related use cases.
- **Methods**: Generate invoice, record payment, cancel, fetch by id, list by patient, generate PDF.
- **Dependencies**: None (interface).
- **Notes**: Lacks JavaDoc; method signatures rely on primitive types without validation annotations.

### com.gi.billingservice.service.impl.BillingServiceImpl

- **Responsibility**: Implements business operations for invoices.
- **Key Attributes**: `InvoiceRepository`, `InvoiceMapper`.
- **Logic**:
  - `generateInvoice` constructs invoices using hard-coded `ConsultationDTO` data and mock patient name, then saves via repository.
  - `recordPayment` ensures status is `PENDING_PAYMENT` before marking as `PAID`.
  - `cancelInvoice` sets status to `CANCELLED`.
  - `findById` and `listInvoicesByPatient` delegate to repository.
  - `generatePdf` throws `UnsupportedOperationException`.
- **Dependencies**:
  - Uses `InvoiceRepository` for persistence.
  - Maps entities to DTO via `InvoiceMapper`.
  - Throws `ResourceNotFoundException` and `BusinessException`.
- **Patterns**: Simple transactional service with manual mock data; no integration with `ConsultationClient` or `PatientClient`.
- **Anti-patterns**: Hard-coded consultation/patient data, repeated “Mock Patient” string, and unimplemented PDF method.

### com.gi.billingservice.mapper.InvoiceMapper

- **Responsibility**: Converts `Invoice` entities to `InvoiceDTO`.
- **Key Methods**: `toDTO`.
- **Dependencies**: None beyond Lombok annotations.
- **Notes**: One-way mapping only; lacks null checks and mapping for future fields.

### com.gi.billingservice.repository.InvoiceRepository

- **Responsibility**: Spring Data JPA repository for invoices.
- **Key Methods**: `findByConsultationId`, `findByPatientId`, `findByInvoiceDateBetween`.
- **Dependencies**: Extends `JpaRepository`.
- **Notes**: Standard derived queries; no custom JPQL.

### com.gi.billingservice.model.entity.Invoice

- **Responsibility**: Persistence model for invoices.
- **Fields**: id, invoiceNumber, patientId, consultationId, cabinetId, doctorId, invoiceDate, amount, status, paymentMethod, paymentDate.
- **Annotations**: `@Entity`, JPA column metadata, Lombok `@Data`.
- **Business Rules**: Unique invoice numbers and non-null constraints ensure minimum data integrity.
- **Notes**: No audit fields; uses `GenerationType.IDENTITY`.

### com.gi.billingservice.model.dto.response.InvoiceDTO

- **Responsibility**: API response shape for invoices.
- **Fields**: Mirrors `Invoice` plus `patientName`.
- **Notes**: Simple Lombok DTO; no validation.

### com.gi.billingservice.model.dto.request.ConsultationDTO

- **Responsibility**: Represents consultation data fetched from another service.
- **Fields**: id, patientId, doctorId, cabinetId.
- **Usage**: Instantiated manually in service; no actual client integration yet.

### com.gi.billingservice.model.enums.InvoiceStatus

- **Values**: `PENDING_PAYMENT`, `PAID`, `CANCELLED`.
- **Usage**: Controls invoice lifecycle states.

### com.gi.billingservice.model.enums.PaymentMethod

- **Values**: `CASH`, `CREDIT_CARD`, `INSURANCE`, `CHECK`.
- **Usage**: Selected during payment recording.

### com.gi.billingservice.exception.BusinessException

- **Responsibility**: Signals domain rule violations (e.g., paying non-pending invoices).

### com.gi.billingservice.exception.ResourceNotFoundException

- **Responsibility**: Thrown when requested invoice does not exist.

### com.gi.billingservice.exception.GlobalExceptionHandler

- **Responsibility**: Maps exceptions to HTTP responses.
- **Handlers**:
  - `ResourceNotFoundException` → 404 with message.
  - `BusinessException` → 400 with message.
  - Generic `Exception` → 500 with fixed text.
- **Pattern**: Spring `@RestControllerAdvice`.
- **Improvement**: Include structured error body and logging.

### com.gi.billingservice.client.PatientClient

- **Responsibility**: Contract for future patient service integration.
- **Current State**: Interface without implementation.

### com.gi.billingservice.client.ConsultationClient

- **Responsibility**: Contract to fetch consultation details.
- **Current State**: Unimplemented; service uses hard-coded data instead.

### com.gi.billingservice.BillingServiceApplicationTests

- **Responsibility**: Smoke test for Spring context.
- **Method**: `contextLoads`.
- **Notes**: No functional tests exist.

## Business Logic Flow

1. **Incoming Request**: An API call hits `BillingController`.
2. **Service Delegation**: Controller invokes the corresponding method on `BillingServiceImpl`.
3. **Data Preparation**:
   - On invoice creation, a mock `ConsultationDTO` is instantiated instead of calling external services.
   - Patient name is hard-coded as “Mock Patient”.
4. **Persistence**: The service uses `InvoiceRepository` to save or fetch `Invoice` entities.
5. **Mapping**: Entities are converted to `InvoiceDTO` via `InvoiceMapper`.
6. **Response**: DTOs are returned to the controller, which wraps them in `ResponseEntity`.
7. **Exception Handling**: Any thrown `ResourceNotFoundException` or `BusinessException` is translated by `GlobalExceptionHandler`.

## Proposed Improvements

1. **Integrate External Clients**: Implement `ConsultationClient` and `PatientClient` using OpenFeign or RestTemplate to replace mock data, ensuring accurate patient and consultation metadata.
2. **Introduce Validation & DTOs**: Add request DTOs with Jakarta Bean Validation annotations for controller methods to prevent invalid IDs or amounts from reaching the service layer.
3. **Implement PDF Generation**: Fulfill `generatePdf` by integrating a PDF library (e.g., iText) and returning appropriate HTTP headers.
4. **Centralize Messages**: Use `MessageSource` configured in application.yml plus the existing `messages/messages*.properties` to internationalize exception texts instead of hard-coded English strings.
5. **Error Payload Standardization**: Enhance `GlobalExceptionHandler` to return structured JSON with error codes and timestamps.
6. **Testing**: Add unit tests for `BillingServiceImpl` and integration tests for `BillingController` using MockMvc.
7. **Replace Lombok @Data on Entity**: Favor explicit getters/setters with `@Getter` and `@Setter` to avoid unwanted equals/hashCode behavior on entities.

## Summary

The billing service currently provides CRUD-like invoice operations backed by Spring Data JPA and exposes them over REST. Mocked dependencies and placeholder implementations indicate the service is an early prototype. Key improvements needed:

- Implement real consultation and patient integrations.
- Add validation, error standardization, and completed PDF generation.
- Strengthen testing and clean up unused configuration stubs.
- Improve internationalization and domain modeling practices.
