# Analytics Service Overview

## Introduction

The Analytics Service aggregates clinic-level and global KPIs from persisted statistics records. It exposes REST endpoints via Spring Boot to retrieve KPI slices filtered by date ranges and period hints. The architecture follows a layered design: controller → service → repositories → JPA entities backed by H2 (default) or MySQL.

## Class-by-Class Analysis

### [`com.gi.analyticsservice.AnalyticsServiceApplication`](../src/main/java/com/gi/analyticsservice/AnalyticsServiceApplication.java)

- **Responsibility**: Bootstraps Spring Boot with component scanning and auto-configuration.
- **Key Methods**: `main` launches the application.
- **Dependencies**: Spring Boot auto-configured beans.
- **Patterns**: Standard Spring Boot entry point.

### [`com.gi.analyticsservice.controller.StatisticsController`](../src/main/java/com/gi/analyticsservice/controller/StatisticsController.java)

- **Responsibility**: REST interface at `/api/statistics`.
- **Endpoints**:
  - `GET /clinic/{clinicId}/kpis` → returns clinic KPIs.
  - `GET /global/kpis` → returns global KPIs.
- **Dependencies**: [`com.gi.analyticsservice.service.StatisticsService`](../src/main/java/com/gi/analyticsservice/service/StatisticsService.java).
- **Business Logic**: Builds [`StatisticsCriteria`](../src/main/java/com/gi/analyticsservice/model/dto/request/StatisticsCriteria.java) from query params and delegates to service.
- **Pattern**: Thin controller; validation missing (anti-pattern).

### [`com.gi.analyticsservice.service.StatisticsService`](../src/main/java/com/gi/analyticsservice/service/StatisticsService.java)

- **Responsibility**: Contract for KPI retrieval and daily computations.
- **Methods**:
  - `getClinicKpis(Long, StatisticsCriteria)`
  - `getGlobalKpis(StatisticsCriteria)`
  - `calculateDailyStatistics(Long, LocalDate)` (stub).
- **Dependencies**: Implemented by [`StatisticsServiceImpl`](../src/main/java/com/gi/analyticsservice/service/impl/StatisticsServiceImpl.java).

### [`com.gi.analyticsservice.service.impl.StatisticsServiceImpl`](../src/main/java/com/gi/analyticsservice/service/impl/StatisticsServiceImpl.java)

- **Responsibility**: Fetches and maps statistics entities to DTOs.
- **Key Attributes**: `clinicStatisticsRepository`, `globalStatisticsRepository`.
- **Methods**:
  - `getClinicKpis`: Queries [`ClinicStatisticsRepository`](../src/main/java/com/gi/analyticsservice/repository/ClinicStatisticsRepository.java) and maps to [`ClinicKpiDTO`](../src/main/java/com/gi/analyticsservice/model/dto/response/ClinicKpiDTO.java).
  - `getGlobalKpis`: Queries [`GlobalStatisticsRepository`](../src/main/java/com/gi/analyticsservice/repository/GlobalStatisticsRepository.java) and maps to [`GlobalKpiDTO`](../src/main/java/com/gi/analyticsservice/model/dto/response/GlobalKpiDTO.java).
  - `calculateDailyStatistics`: TODO placeholder (anti-pattern: unimplemented contract).
- **Business Logic Flow**: Date-filtered repository calls, stream mapping, DTO population.
- **Patterns**: Simple service pattern; lacks caching or validation.

### [`com.gi.analyticsservice.repository.ClinicStatisticsRepository`](../src/main/java/com/gi/analyticsservice/repository/ClinicStatisticsRepository.java)

- **Responsibility**: Data access for [`ClinicStatistics`](../src/main/java/com/gi/analyticsservice/model/entity/ClinicStatistics.java) via JPA.
- **Key Method**: `findByIdClinicIdAndIdDateBetween` filtering composite key fields.
- **Dependencies**: Extends `JpaRepository`.

### [`com.gi.analyticsservice.repository.GlobalStatisticsRepository`](../src/main/java/com/gi/analyticsservice/repository/GlobalStatisticsRepository.java)

- **Responsibility**: Data access for [`GlobalStatistics`](../src/main/java/com/gi/analyticsservice/model/entity/GlobalStatistics.java).
- **Key Method**: `findByDateBetween`.

### DTOs

- [`ClinicKpiDTO`](../src/main/java/com/gi/analyticsservice/model/dto/response/ClinicKpiDTO.java): Holds clinic KPI snapshot (date, consultations, new patients, revenue).
- [`GlobalKpiDTO`](../src/main/java/com/gi/analyticsservice/model/dto/response/GlobalKpiDTO.java): Holds global KPI snapshot (date, active clinics, user and appointment counts).
- [`StatisticsCriteria`](../src/main/java/com/gi/analyticsservice/model/dto/request/StatisticsCriteria.java): Carries filtering constraints, including optional [`PeriodFilter`](../src/main/java/com/gi/analyticsservice/model/enums/PeriodFilter.java).

### Entities

- [`ClinicStatistics`](../src/main/java/com/gi/analyticsservice/model/entity/ClinicStatistics.java):
  - Composite ID [`ClinicStatisticsId`](../src/main/java/com/gi/analyticsservice/model/entity/ClinicStatisticsId.java) (clinicId + date).
  - Fields: consultations, new patients, revenue.
- [`GlobalStatistics`](../src/main/java/com/gi/analyticsservice/model/entity/GlobalStatistics.java):
  - Primary key date.
  - Fields: active clinics, users, appointments.

### Enums

- [`PeriodFilter`](../src/main/java/com/gi/analyticsservice/model/enums/PeriodFilter.java): DAY/WEEK/MONTH hints; currently unused downstream.

### Tests

- [`AnalyticsServiceApplicationTests`](../src/test/java/com/gi/analyticsservice/AnalyticsServiceApplicationTests.java): Context smoke test only.

## Business Logic Flow

1. Client issues HTTP GET to controller.
2. Controller constructs `StatisticsCriteria` using path/query parameters.
3. Service implementation queries repositories based on start/end dates and (implicitly) clinic ID.
4. Repository returns entity lists filtered by date.
5. Service streams entities, mapping to DTOs.
6. DTOs are serialized as JSON responses.

No aggregation beyond direct entity mapping is present; `PeriodFilter` is ignored. Persistence depends on preloaded statistics data.

## Proposed Improvements

1. **Criteria Validation**: Ensure `startDate`/`endDate` defaults (e.g., last 30 days) and validate ordering to prevent empty queries.
2. **PeriodFilter Usage**: Implement grouping logic (e.g., weekly aggregation) to honor `period`.
3. **DTO Mapping Layer**: Introduce mapper utilities (e.g., MapStruct) to reduce manual mapping.
4. **calculateDailyStatistics Implementation**: Integrate with upstream services or scheduled jobs for data freshness.
5. **Error Handling**: Return meaningful HTTP errors when parameters are invalid or data missing.
6. **Caching**: Add caching for frequently accessed KPI windows to reduce DB load.

## Summary

The service currently exposes read-only KPI endpoints backed by JPA repositories, mapping stored statistics entities directly to DTO responses. Key pending improvements:

- Implement real calculation logic and scheduling.
- Enforce and honor filtering semantics (`PeriodFilter`, default ranges).
- Harden validation and error handling.
- Introduce automated mapping/testing layers for maintainability.
