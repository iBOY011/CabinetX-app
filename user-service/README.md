# User Service

## 1. Service Overview

The **User Service** is a Spring Boot microservice responsible for user lifecycle operations in the CabinetX platform. It supports

- creating users for the `ADMIN`, `DOCTOR`, and `SECRETARY` roles,
- updating basic user attributes,
- activating/deactivating accounts, and
- listing users by role.

The current implementation stores passwords in plaintext, does not hash credentials, and does not yet wire clinic identifiers into the profile entities even though the DTO contains a `clinicId` field.

## 2. API Endpoints

All routes are rooted at `/api/users`.

### 2.1 Create User – `POST /api/users`

Creates a new user and, for doctors or secretaries, inserts an empty profile row tied to the user ID.

#### Request Body

| Field        | Type   | Required | Notes                                                                 |
|--------------|--------|----------|-----------------------------------------------------------------------|
| `firstName`  | string | yes      | Must be non-blank (Jakarta validation).                               |
| `lastName`   | string | yes      | Must be non-blank.                                                    |
| `login`      | string | yes      | Must match the email-style regex defined on `CreateUserRequest`.      |
| `password`   | string | yes      | At least eight chars, contains letters and numbers; stored as-is.     |
| `phoneNumber`| string | no       | Optional 10-digit numeric string.                                     |
| `role`       | enum   | yes      | One of `ADMIN`, `DOCTOR`, `SECRETARY`.                                |

```json
{
  "firstName": "Jane",
  "lastName": "Doe",
  "login": "jane.doe@example.com",
  "password": "Password123",
  "phoneNumber": "0612345678",
  "role": "SECRETARY"
}
```

#### Responses – Create User

| Status | Description                                   |
|--------|-----------------------------------------------|
| 201    | Returns the persisted `UserDTO`.              |
| 400    | Validation failure or other runtime problem. |

Example success payload:

```json
{
  "id": 1,
  "firstName": "Jane",
  "lastName": "Doe",
  "login": "jane.doe@example.com",
  "password": null,
  "phoneNumber": "0612345678",
  "role": "SECRETARY",
  "active": true
}
```

### 2.2 Get User – `GET /api/users/{id}`

Fetches a single user by identifier.

#### Path Parameter

`id` (long)

#### Responses – Get User

- `200 OK` – JSON representation of the user (including plaintext password).
- `400 Bad Request` – if the ID does not exist (the service currently throws `RuntimeException("User not found")`, which is mapped to 400).

### 2.3 Update User – `PUT /api/users/{id}`

Overwrites mutable fields on the target user. The request body follows the `UserDTO` shape.

| Field        | Handled? | Notes                                                                 |
|--------------|----------|-----------------------------------------------------------------------|
| `id`         | ignored  | Path variable determines the record.                                  |
| `firstName`  | yes      | Replaced with provided value.                                         |
| `lastName`   | yes      | Replaced.                                                             |
| `login`      | no       | The implementation never updates this field.                          |
| `password`   | yes      | Stored exactly as provided.                                           |
| `phoneNumber`| yes      | Replaced.                                                             |
| `role`       | yes      | Updated without adjusting profile rows.                               |
| `clinicId`   | no       | Value is ignored.                                                     |
| `active`     | yes      | Toggles active flag, though dedicated endpoints exist.                |

#### Responses – Update User

- `200 OK` with updated DTO.
- `400 Bad Request` when the user does not exist.

### 2.4 Activate User – `POST /api/users/{id}/activate`

Sets `active=true` for the given user.

- Returns `200 OK` with the updated DTO.
- Throws `BusinessException` (translated to `400 Bad Request`) when the user is already active.
- Throws `RuntimeException("User not found")` (also 400) when the ID is unknown.

### 2.5 Deactivate User – `POST /api/users/{id}/deactivate`

Sets `active=false`.

- Returns `200 OK` with the updated DTO.
- Throws `BusinessException` (400) when already inactive.
- Throws `RuntimeException("User not found")` (400) for missing IDs.

### 2.6 List Users by Role – `GET /api/users/role/{role}`

Returns all users assigned to the specified `UserRole`.

- `role` is a path variable that must match the enum exactly (e.g., `ADMIN`).
- Response is a JSON array of `UserDTO` objects (passwords included; `clinicId` is always `null` in the current code path).
- Any invalid role string or repository error surfaces as `400 Bad Request`.

> **Note:** There is no `/clinic/{clinicId}` endpoint in the current controller even though a `clinicId` column exists in the repositories. The clinic association has not been implemented yet.

## 3. Running the Service

### 3.1 Prerequisites

- JDK 21+
- Maven 3.9+ (or use the bundled `mvnw` scripts)

### 3.2 Local Run

```sh
./mvnw spring-boot:run
```

The service listens on `http://localhost:8085` and uses an in-memory H2 database (`jdbc:h2:mem:userdb`). The H2 console is exposed at `http://localhost:8085/h2-console` with username `sa` and blank password.

### 3.3 Build an Executable JAR

```sh
./mvnw clean package
java -jar target/user-service-0.0.1-SNAPSHOT.jar
```

### 3.4 Containerizing (optional)

The repo does not include a Dockerfile, but you can create one like below after running `./mvnw clean package`:

```Dockerfile
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY target/user-service-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8085
ENTRYPOINT ["java", "-jar", "app.jar"]
```

```sh
docker build -t user-service .
docker run -p 8085:8085 --name user-service user-service
```
