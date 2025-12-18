# Medical Record Service

## Service Overview

The Medical Record Service centralizes longitudinal patient information, document artifacts, and consultation history. It exposes RESTful endpoints implemented by [`com.gi.medicalrecordservice.controller.MedicalRecordController`](src/main/java/com/gi/medicalrecordservice/controller/MedicalRecordController.java), delegates business rules to [`com.gi.medicalrecordservice.service.impl.MedicalRecordServiceImpl`](src/main/java/com/gi/medicalrecordservice/service/impl/MedicalRecordServiceImpl.java), persists data through Spring Data repositories, and enriches responses with consultations retrieved via [`com.gi.medicalrecordservice.client.ConsultationServiceClient`](src/main/java/com/gi/medicalrecordservice/client/ConsultationServiceClient.java). Demo records are seeded at startup by [`com.gi.medicalrecordservice.config.MedicalRecordInitializer`](src/main/java/com/gi/medicalrecordservice/config/MedicalRecordInitializer.java), enabling immediate exploration.

## API Endpoints

### Endpoint Summary

| Method | Path | Purpose |
| ------ | ---- | ------- |
| POST | `/api/records/patients/{patientId}` | Open or return an existing medical record for the patient |
| PUT | `/api/records/{recordId}` | Update medical history attributes of a record |
| GET | `/api/records/patients/{patientId}` | Retrieve the complete record with documents and consultations |
| POST | `/api/records/{recordId}/documents` | Upload a medical document (multipart) |
| DELETE | `/api/records/documents/{documentId}` | Delete a document by identifier |
| GET | `/api/records/patients/{patientId}/consultations` | Fetch consultation history via consultation-service |

### Detailed Contracts

#### POST `/api/records/patients/{patientId}`

- **Purpose**: Idempotently create a record if it does not exist.
- **Path parameters**: `patientId` (Long) — unique identifier of the patient.
- **Request body**: None.
- **Responses**:
  - `201 Created` → `MedicalRecordDTO`.
  - `500 Internal Server Error` → fallback message if persistence fails.
- **Notes**: Returns existing data when the record already exists.

#### PUT `/api/records/{recordId}`

- **Purpose**: Update textual medical metadata.
- **Path parameters**: `recordId` (Long).
- **Request body** (`application/json`):

  | Field | Type | Description |
  | ----- | ---- | ----------- |
  | `medicalHistory` | String | Past pathologies and surgeries |
  | `allergies` | String | Known allergies |
  | `treatments` | String | Current treatments |
  | `habits` | String | Lifestyle notes |

- **Responses**:
  - `200 OK` → updated `MedicalRecordDTO`.
  - `404 Not Found` → when record ID is unknown.
- **Validation**: `@Valid` applied, but DTO currently lacks explicit constraints.

#### GET `/api/records/patients/{patientId}`

- **Purpose**: Retrieve a fully enriched medical record.
- **Path parameters**: `patientId` (Long).
- **Responses**:
  - `200 OK` → `MedicalRecordDTO`.
  - `404 Not Found` → when no record exists for the patient.

#### POST `/api/records/{recordId}/documents`

- **Purpose**: Attach a binary document to a record.
- **Path parameters**: `recordId` (Long).
- **Request** (`multipart/form-data`):

  | Part | Type | Description |
  | ---- | ---- | ----------- |
  | `file` | Binary | Document file content |
  | `type` | Enum (`DocumentType`) | Document classification (e.g., `ANALYSIS`) |

- **Responses**:
  - `201 Created` → `MedicalDocumentDTO`.
  - `400 Bad Request` → when file cannot be read.
  - `404 Not Found` → when record ID is invalid.

#### DELETE `/api/records/documents/{documentId}`

- **Purpose**: Remove a stored document.
- **Path parameters**: `documentId` (Long).
- **Responses**:
  - `204 No Content` on success.
  - `404 Not Found` if the document does not exist.

#### GET `/api/records/patients/{patientId}/consultations`

- **Purpose**: Proxy consultation history from consultation-service.
- **Path parameters**: `patientId` (Long).
- **Responses**:
  - `200 OK` → `List<ConsultationInfo>`.
  - `200 OK` with empty list when remote service is unavailable (logged warning).
  - `502 Bad Gateway` would be desirable but is not currently implemented.

### Response Example

````json
{
  "id": 1,
  "patientId": 42,
  "medicalHistory": "Hypertension",
  "allergies": "Penicillin",
  "treatments": "Atenolol",
  "habits": "Non-smoker",
  "creationDate": "2024-01-10T09:15:00",
  "lastUpdate": "2024-01-12T10:30:00",
  "documents": [
    {
      "id": 5,
      "fileName": "lab-results.pdf",
      "fileType": "application/pdf",
      "fileUrl": null,
      "documentType": "ANALYSIS",
      "additionDate": "2024-01-12T10:00:00"
    }
  ],
  "consultationHistory": [
    {
      "id": 11,
      "consultationDate": "2023-12-01T11:00:00",
      "type": "CONSULTATION",
      "diagnosis": "Bronchitis",
      "treatment": "Antibiotics"
    }
  ]
}
````

## How to Run

### Prerequisites
- JDK 21+
- Maven Wrapper (bundled) or Maven 3.9+
- (Optional) Running consultation-service instance if consultation history must be populated.

### Local Execution

1. Install dependencies and run tests:
   ````

   sh
   ./mvnw clean verify

   ````
2. Start the service (H2 in-memory DB by default):
   ````

   sh
   SPRING_PROFILES_ACTIVE=dev ./mvnw spring-boot:run

   ````
3. Access the API at `http://localhost:8080`. Demo records for patient IDs `1..3` are preloaded by [`MedicalRecordInitializer`](src/main/java/com/gi/medicalrecordservice/config/MedicalRecordInitializer.java).

### Container Image

Use Spring Boot’s built-in image support (no Dockerfile required):

1. Build the OCI image:
   ````

   sh
   ./mvnw spring-boot:build-image -Dspring-boot.build-image.imageName=medical-record-service:latest

   ````
2. Run the container:
   ````

   sh
   docker run --rm -p 8080:8080 \
     -e SPRING_PROFILES_ACTIVE=prod \
     medical-record-service:latest

   ````
3. Configure database credentials and Feign target URLs through environment variables or externalized configuration when running in production.

