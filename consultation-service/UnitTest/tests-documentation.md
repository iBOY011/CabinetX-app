# Consultation Service - Documentation des Tests Unitaires

## 📊 Résumé

| Catégorie | Nombre de Tests | Statut |
|-----------|-----------------|--------|
| **Total** | **96 tests** | ✅ Tous passent |
| Application | 2 | ✅ |
| Entités | 11 | ✅ |
| DTOs | 25 | ✅ |
| Enums | 4 | ✅ |
| Mappers | 13 | ✅ |
| Service | 22 | ✅ |
| Controller | 19 | ✅ |

---

## 🏗️ Architecture des Tests

```
src/test/java/com/gi/consultationservice/
├── ConsultationServiceApplicationTests.java (2 tests)
├── config/
│   └── TestSecurityConfig.java (configuration)
├── dto/
│   ├── ConsultationDTOTest.java (7 tests)
│   ├── ConsultationSummaryDTOTest.java (6 tests)
│   ├── DailyStatDTOTest.java (6 tests)
│   └── MedecinWeeklyStatsDTOTest.java (6 tests)
├── entities/
│   ├── ConsultationTest.java (6 tests)
│   └── ConsultationCreatedEventTest.java (5 tests)
├── enums/
│   └── ConsultationTypeTest.java (4 tests)
├── mappers/
│   └── ConsultationMapperTest.java (13 tests)
├── service/
│   └── ConsultationServiceTest.java (22 tests)
└── web/
    └── ConsultationControllerTest.java (19 tests)
```

---

## 📋 Détail des Tests par Catégorie

### 1. Tests d'Application (`ConsultationServiceApplicationTests`)

| Test | Description |
|------|-------------|
| `applicationClassExists` | Vérifie que la classe principale existe |
| `mainMethodExists` | Vérifie que la méthode main existe |

> **Note**: Le contexte complet n'est pas chargé pour éviter les problèmes avec le CommandLineRunner qui seed la base de données.

---

### 2. Tests d'Entités

#### `ConsultationTest` (6 tests)
| Test | Description |
|------|-------------|
| `builder_shouldCreateConsultation` | Création via builder |
| `builder_shouldCreateWithDefaultValues` | Valeurs par défaut |
| `setters_shouldUpdateValues` | Test des setters |
| `consultationType_shouldBeCorrect` | Type consultation |
| `toString_shouldContainFields` | Format toString |
| `archived_shouldBeSettable` | Champ archived |

#### `ConsultationCreatedEventTest` (5 tests)
| Test | Description |
|------|-------------|
| `builder_shouldCreateEvent` | Création d'événement |
| `builder_shouldCreateWithAllFields` | Tous les champs |
| `setters_shouldUpdateValues` | Modification des valeurs |
| `toString_shouldContainFields` | Format toString |
| `noArgsConstructor_shouldCreateEmptyEvent` | Constructeur vide |

---

### 3. Tests des DTOs

#### `ConsultationDTOTest` (7 tests)
| Test | Description |
|------|-------------|
| `builder_shouldCreateDTO` | Création via builder |
| `builder_shouldCreateWithAllFields` | Tous les champs |
| `setters_shouldUpdateValues` | Modification |
| `equals_shouldCompareByValue` | Comparaison equals |
| `hashCode_shouldBeConsistent` | Cohérence hashCode |
| `toString_shouldContainFields` | Format toString |
| `noArgsConstructor_shouldCreateEmptyDTO` | Constructeur vide |

#### `ConsultationSummaryDTOTest` (6 tests)
| Test | Description |
|------|-------------|
| `builder_shouldCreateSummary` | Création résumé |
| `builder_shouldCreateWithAllFields` | Tous les champs |
| `setters_shouldUpdateValues` | Modification |
| `equals_shouldCompareByValue` | Comparaison |
| `hashCode_shouldBeConsistent` | Cohérence hash |
| `toString_shouldContainFields` | Format toString |

#### `DailyStatDTOTest` (6 tests) - *@Value immutable*
| Test | Description |
|------|-------------|
| `shouldCreateWithAllFields` | Création complète |
| `shouldBeImmutable` | Vérification immutabilité |
| `equals_shouldCompareByValue` | Égalité par valeur |
| `hashCode_shouldBeConsistent` | Cohérence hashCode |
| `toString_shouldContainFields` | Format toString |
| `getters_shouldReturnCorrectValues` | Getters corrects |

#### `MedecinWeeklyStatsDTOTest` (6 tests) - *@Value immutable*
| Test | Description |
|------|-------------|
| `shouldCreateWithAllFields` | Création complète |
| `shouldContainDailyStats` | Stats quotidiennes |
| `equals_shouldCompareByValue` | Égalité par valeur |
| `hashCode_shouldBeConsistent` | Cohérence hashCode |
| `toString_shouldContainFields` | Format toString |
| `presenceRate_shouldBeDouble` | Type taux présence |

---

### 4. Tests d'Enumération

#### `ConsultationTypeTest` (4 tests)
| Test | Description |
|------|-------------|
| `shouldHaveConsultationType` | Valeur CONSULTATION |
| `shouldHaveControleType` | Valeur CONTROLE |
| `shouldHaveExactlyTwoValues` | Exactement 2 valeurs |
| `valueOf_shouldReturnCorrectEnum` | Conversion string |

---

### 5. Tests du Mapper

#### `ConsultationMapperTest` (13 tests)
| Test | Description |
|------|-------------|
| `toEntity_shouldMapAllFields` | DTO → Entity |
| `toEntity_shouldHandleNullInput` | Null safety |
| `toDTO_shouldMapAllFields` | Entity → DTO |
| `toDTO_shouldHandleNullInput` | Null safety |
| `toSummary_shouldMapAllFields` | Entity → Summary |
| `toSummary_shouldHandleNullInput` | Null safety |
| `toDTOList_shouldMapAllElements` | Liste DTO |
| `toDTOList_shouldHandleEmptyList` | Liste vide |
| `toDTOList_shouldHandleNullList` | Null safety |
| `toSummaryList_shouldMapAllElements` | Liste Summary |
| `toSummaryList_shouldHandleEmptyList` | Liste vide |
| `toSummaryList_shouldHandleNullList` | Null safety |
| `toDTO_shouldPreserveAllFields` | Conservation champs |

---

### 6. Tests du Service

#### `ConsultationServiceTest` (22 tests)
| Test | Description |
|------|-------------|
| `creerConsultation_shouldReturnCreatedDTO` | Création consultation |
| `modifierConsultation_shouldUpdateAndReturn` | Mise à jour |
| `modifierConsultation_shouldThrowWhenNotFound` | Exception si non trouvé |
| `supprimerConsultation_shouldDeleteById` | Suppression |
| `supprimerConsultation_shouldThrowWhenNotFound` | Exception si non trouvé |
| `trouverParId_shouldReturnDTO` | Recherche par ID |
| `trouverParId_shouldThrowWhenNotFound` | Exception si non trouvé |
| `trouverParRendezVous_shouldReturnDTO` | Recherche par RDV |
| `trouverParRendezVous_shouldThrowWhenNotFound` | Exception si non trouvé |
| `listerParPatient_shouldReturnSummaries` | Liste par patient |
| `listerToutes_shouldReturnAllDTOs` | Liste complète |
| `listerRecentsParMedecin_shouldReturnLimitedResults` | Récentes par médecin |
| `listerParMedecinEtJour_shouldReturnDailyConsultations` | Par médecin et jour |
| `listerParMedecinEtPeriode_shouldReturnPeriodConsultations` | Par médecin et période |
| `statsMedecinWeekly_shouldReturnWeeklyStats` | Stats hebdomadaires |
| `statsMedecinWeekly_shouldUseDefaultDatesWhenNull` | Dates par défaut |
| `statsMedecinWeekly_shouldThrowWhenEndDateBeforeStartDate` | Validation dates |
| `listerParMedecinPaged_shouldReturnPagedSummaries` | Pagination |
| `listerParMedecinPaged_shouldApplyFilters` | Filtres |
| `listerToutes_shouldReturnEmptyListWhenNoConsultations` | Liste vide |
| `listerParPatient_shouldReturnEmptyListWhenNoConsultations` | Historique vide |
| `handleConsultationCompletion_shouldPublishEvent` | Publication événement |

---

### 7. Tests du Controller

#### `ConsultationControllerTest` (19 tests)
| Test | Description |
|------|-------------|
| `creerConsultation_shouldReturnCreatedConsultation` | POST /api/consultations |
| `modifierConsultation_shouldReturnUpdatedConsultation` | PUT /api/consultations/{id} |
| `supprimerConsultation_shouldCallService` | DELETE /api/consultations/{id} |
| `trouverParId_shouldReturnConsultation` | GET /api/consultations/{id} |
| `trouverParRendezVous_shouldReturnConsultation` | GET /rendezvous/{rendezVousId} |
| `listerToutes_shouldReturnAllConsultations` | GET /api/consultations |
| `historiquePatient_shouldReturnPatientHistory` | GET /patients/{patientId}/historique |
| `consultationsJour_shouldReturnDailyConsultations` | GET /medecins/{id}/jour |
| `consultationsPeriode_shouldReturnPeriodConsultations` | GET /medecins/{id}/periode |
| `consultationsRecents_shouldReturnRecentConsultations` | GET /medecins/{id}/recent |
| `consultationsRecents_shouldUseDefaultLimitWhenNegative` | Limite par défaut |
| `consultationsPaged_shouldReturnPagedConsultations` | GET /medecins/{id} (paged) |
| `consultationsPaged_withFilters_shouldApplyFilters` | Filtres pagination |
| `statsMedecinWeekly_shouldReturnStats` | GET /stats/medecin/{id}/weekly |
| `statsMedecinWeekly_withNullDates_shouldCallService` | Dates nulles |
| `listerToutes_shouldReturnEmptyListWhenNoConsultations` | Liste vide |
| `historiquePatient_shouldReturnEmptyListWhenNoHistory` | Historique vide |
| `consultationsPaged_withDefaultSort_shouldParseCorrectly` | Tri par défaut |
| `consultationsPaged_withAscSort_shouldParseCorrectly` | Tri ascendant |

---

## ⚙️ Configuration des Tests

### `application-test.yml`
```yaml
spring:
  cloud:
    config:
      enabled: false
  datasource:
    url: jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1
    driver-class-name: org.h2.Driver
  jpa:
    hibernate:
      ddl-auto: create-drop
    database-platform: org.hibernate.dialect.H2Dialect
  kafka:
    bootstrap-servers: localhost:9092

eureka:
  client:
    enabled: false
```

### `TestSecurityConfig.java`
Configuration de sécurité simplifiée pour les tests qui permet tous les accès et utilise le profil `test`.

---

## 🧪 Technologies Utilisées

- **JUnit 5**: Framework de test principal
- **Mockito**: Mocking des dépendances
- **AssertJ**: Assertions fluides
- **H2 Database**: Base de données en mémoire pour les tests
- **Spring Boot Test**: Support Spring pour les tests

---

## 🔧 Exécution des Tests

```bash
# Exécuter tous les tests
cd consultation-service
mvn test

# Exécuter un test spécifique
mvn test -Dtest=ConsultationServiceTest

# Exécuter avec rapport de couverture
mvn test jacoco:report
```

---

## 📈 Couverture de Code

Les tests couvrent :
- ✅ Toutes les entités (Consultation, ConsultationCreatedEvent)
- ✅ Tous les DTOs (ConsultationDTO, ConsultationSummaryDTO, DailyStatDTO, MedecinWeeklyStatsDTO)
- ✅ Tous les enums (ConsultationType)
- ✅ Tous les mappers (ConsultationMapper)
- ✅ Toutes les méthodes du service (ConsultationService)
- ✅ Tous les endpoints REST (ConsultationController)

---

## 📝 Notes Importantes

1. **Clients Feign Mockés**: Les clients externes (PrescriptionClient, AppointmentClient, NotificationClient, UserClient, PatientClient) sont mockés dans les tests de service.

2. **Kafka Mocké**: Le `ConsultationCompletedEventPublisher` est mocké pour éviter les dépendances sur Kafka.

3. **Specifications JPA**: Le service utilise les Specifications JPA pour le filtrage dynamique, testés avec des mocks appropriés.

4. **DTOs Immutables**: `DailyStatDTO` et `MedecinWeeklyStatsDTO` utilisent `@Value` de Lombok, rendant les objets immutables.

5. **Gestion des Null**: Tous les mappers gèrent correctement les entrées null.

---

*Documentation générée le 15 janvier 2026*
*Version: 1.0.0*
