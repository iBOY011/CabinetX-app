# Billing Service

## Service Overview

Billing Service manages the lifecycle of medical consultation invoices. It creates invoices, records payments, cancels invoices, retrieves invoice data, lists invoices per patient, and can emit binary PDF representations (currently unimplemented). It uses Spring Boot, Spring Web MVC, and Spring Data JPA to expose REST endpoints and persist `Invoice` entities in the configured datasource.

## API Endpoints

### POST `/api/billing/invoices`

| Item | Details |
| --- | --- |
| Purpose | Create a new invoice for a consultation. |
| Query Params | `consultationId` (Long, required), `amount` (BigDecimal, required). |
| Request Body | None. |
| Responses | `200 OK` with `InvoiceDTO`, `400 Bad Request` on validation failure, `404 Not Found` if consultation is missing (future behavior). |

````json
{
  "id": 1,
  "invoiceNumber": "INV-1A2B3C4D",
  "patientId": 1,
  "consultationId": 42,
  "invoiceDate": "2024-05-01T10:15:30",
  "amount": 250.0,
  "status": "PENDING_PAYMENT",
  "paymentMethod": null,
  "paymentDate": null,
  "patientName": "Mock Patient"
}
````

### PUT `/api/billing/invoices/{id}/payment`

| Item | Details |
| --- | --- |
| Purpose | Record payment for an existing invoice. |
| Path Params | `id` (Long). |
| Query Params | `paymentMethod` (enum `PaymentMethod`). |
| Responses | `200 OK` with updated `InvoiceDTO`, `400 Bad Request` if invoice is not pending, `404 Not Found` if invoice missing. |

````json
{
  "id": 1,
  "invoiceNumber": "INV-1A2B3C4D",
  "status": "PAID",
  "paymentMethod": "CASH",
  "paymentDate": "2024-05-02T09:00:00"
}
````

### PUT `/api/billing/invoices/{id}/cancel`

| Item | Details |
| --- | --- |
| Purpose | Cancel an invoice. |
| Path Params | `id` (Long). |
| Responses | `200 OK` with cancelled `InvoiceDTO`, `404 Not Found` if invoice missing. |

### GET `/api/billing/invoices/{id}`

| Item | Details |
| --- | --- |
| Purpose | Fetch a single invoice. |
| Path Params | `id` (Long). |
| Responses | `200 OK` with `InvoiceDTO`, `404 Not Found`. |

### GET `/api/billing/patients/{patientId}/invoices`

| Item | Details |
| --- | --- |
| Purpose | List invoices belonging to a patient. |
| Path Params | `patientId` (Long). |
| Responses | `200 OK` with array of `InvoiceDTO`, `404 Not Found` (future enhancement). |

````json
[
  {
    "id": 1,
    "invoiceNumber": "INV-1A2B3C4D",
    "status": "PAID"
  },
  {
    "id": 2,
    "invoiceNumber": "INV-5E6F7G8H",
    "status": "PENDING_PAYMENT"
  }
]
````

### GET `/api/billing/invoices/{id}/pdf`

| Item | Details |
| --- | --- |
| Purpose | Download PDF representation of an invoice. |
| Path Params | `id` (Long). |
| Responses | `200 OK` with `application/pdf` body (currently throws `UnsupportedOperationException`), `404 Not Found`. |

## How to Run

### Local Execution

Prerequisites: JDK 21.

````bash
./mvnw spring-boot:run
````

The service listens on `http://localhost:8087` as configured in `application.yml`. The H2 console is available at `/h2-console`.

### Docker (after adding a Dockerfile)

1. Create a Dockerfile that packages the Spring Boot jar.
2. Build and run:

````bash
docker build -t billing-service .
docker run -p 8087:8087 --name billing-service billing-service
````
