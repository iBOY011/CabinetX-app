# Documentation des Tests Unitaires - Patient Service

## 📋 Vue d'ensemble

Ce document décrit l'ensemble des tests unitaires implémentés pour le **patient-service**. Ces tests couvrent toutes les couches de l'application : entités, DTOs, enums, mappers, services et contrôleurs.

**Total : 72 tests**

---

## 📁 Structure des Tests

```
src/test/java/com/gi/patientservice/
├── PatientServiceApplicationTests.java      # Test de chargement du contexte
├── dto/
│   └── PatientDTOTest.java                  # Tests du DTO Patient
├── entities/
│   ├── AdresseTest.java                     # Tests de l'entité Adresse
│   ├── PatientEventTest.java                # Tests de l'entité PatientEvent
│   └── PatientTest.java                     # Tests de l'entité Patient
├── enums/
│   ├── EventTypeTest.java                   # Tests de l'enum EventType
│   ├── SexeTest.java                        # Tests de l'enum Sexe
│   └── TypeMutuelleTest.java                # Tests de l'enum TypeMutuelle
├── mappers/
│   └── PatientMapperTest.java               # Tests du mapper Patient
├── security/
│   └── TestSecurityConfig.java              # Configuration de sécurité pour les tests
├── service/
│   └── PatientServiceTest.java              # Tests du service Patient
└── web/
    └── PatientControllerTest.java           # Tests du contrôleur Patient
```

---

## 🧪 Détail des Tests

### 1. PatientServiceApplicationTests (1 test)

| Test | Description |
|------|-------------|
| `contextLoads()` | Vérifie que le contexte Spring Boot se charge correctement avec le profil de test |

---

### 2. PatientDTOTest (6 tests)

Tests de la classe `PatientDTO` qui représente l'objet de transfert de données pour les patients.

| Test | Description |
|------|-------------|
| `testBuilder()` | Vérifie que le pattern Builder fonctionne correctement pour créer un PatientDTO avec tous ses champs |
| `testSetters()` | Vérifie que les setters modifient correctement les valeurs des champs |
| `testEqualsAndHashCode_sameId()` | Vérifie que deux PatientDTO avec le même ID sont considérés égaux |
| `testEqualsAndHashCode_differentId()` | Vérifie que deux PatientDTO avec des IDs différents ne sont pas égaux |
| `testToString()` | Vérifie que la méthode toString() contient les informations essentielles |
| `testNoArgsConstructor()` | Vérifie que le constructeur sans arguments crée une instance valide avec des champs null |

---

### 3. PatientTest (4 tests)

Tests de l'entité `Patient` qui représente un patient dans la base de données.

| Test | Description |
|------|-------------|
| `testPatientBuilder()` | Vérifie la construction d'un Patient avec le Builder Lombok incluant nom, prénom, CIN, date de naissance, sexe, téléphone, email, adresse et mutuelle |
| `testPatientSetters()` | Vérifie que les setters fonctionnent pour modifier les propriétés d'un patient existant |
| `testPatientNoArgsConstructor()` | Vérifie que le constructeur par défaut crée une instance valide |
| `testPatientAllArgsConstructor()` | Vérifie que le constructeur avec tous les arguments initialise correctement tous les champs |

---

### 4. PatientEventTest (4 tests)

Tests de l'entité `PatientEvent` qui enregistre les événements liés aux patients (création, modification, suppression).

| Test | Description |
|------|-------------|
| `testPatientEventBuilder()` | Vérifie la construction d'un événement avec patientId, eventType et timestamp |
| `testPatientEventSetters()` | Vérifie la modification des propriétés d'un événement existant |
| `testPatientEventNoArgsConstructor()` | Vérifie le constructeur par défaut |
| `testPatientEventAllArgsConstructor()` | Vérifie le constructeur avec tous les arguments |

---

### 5. AdresseTest (5 tests)

Tests de l'entité embarquée `Adresse` utilisée pour stocker l'adresse d'un patient.

| Test | Description |
|------|-------------|
| `testAdresseBuilder()` | Vérifie la construction avec rue, ville, codePostal et pays |
| `testAdresseSetters()` | Vérifie la modification des champs d'adresse |
| `testAdresseNoArgsConstructor()` | Vérifie le constructeur par défaut |
| `testAdresseAllArgsConstructor()` | Vérifie le constructeur avec tous les arguments |
| `testAdresseEqualsAndHashCode()` | Vérifie l'égalité de deux adresses identiques |

---

### 6. SexeTest (4 tests)

Tests de l'énumération `Sexe` qui définit les genres possibles.

| Test | Description |
|------|-------------|
| `testSexeValues()` | Vérifie que l'enum contient exactement 3 valeurs : MASCULIN, FEMININ, AUTRE |
| `testSexeValueOf()` | Vérifie que valueOf() retourne la bonne constante pour chaque nom |
| `testSexeName()` | Vérifie que name() retourne le nom correct de chaque constante |
| `testSexeOrdinal()` | Vérifie l'ordre des constantes (MASCULIN=0, FEMININ=1, AUTRE=2) |

---

### 7. TypeMutuelleTest (4 tests)

Tests de l'énumération `TypeMutuelle` qui définit les types de couverture santé.

| Test | Description |
|------|-------------|
| `testTypeMutuelleValues()` | Vérifie que l'enum contient 4 valeurs : AUCUNE, CNSS, CNOPS, PRIVEE |
| `testTypeMutuelleValueOf()` | Vérifie que valueOf() retourne la bonne constante |
| `testTypeMutuelleName()` | Vérifie les noms des constantes |
| `testTypeMutuelleOrdinal()` | Vérifie l'ordre des constantes |

---

### 8. EventTypeTest (4 tests)

Tests de l'énumération `EventType` qui définit les types d'événements patients.

| Test | Description |
|------|-------------|
| `testEventTypeValues()` | Vérifie que l'enum contient 3 valeurs : CREATED, UPDATED, DELETED |
| `testEventTypeValueOf()` | Vérifie que valueOf() retourne la bonne constante |
| `testEventTypeName()` | Vérifie les noms des constantes |
| `testEventTypeOrdinal()` | Vérifie l'ordre des constantes |

---

### 9. PatientMapperTest (9 tests)

Tests du mapper `PatientMapper` qui convertit entre entités Patient et PatientDTO.

| Test | Description |
|------|-------------|
| `toEntity_shouldMapAllFields()` | Vérifie que toEntity() mappe correctement tous les champs du DTO vers l'entité, incluant l'adresse embarquée |
| `toDTO_shouldMapAllFields()` | Vérifie que toDTO() mappe correctement tous les champs de l'entité vers le DTO |
| `toDTOList_shouldMapAllElements()` | Vérifie que toDTOList() convertit une liste d'entités en liste de DTOs |
| `toEntity_shouldHandleNullDTO()` | Vérifie que toEntity() retourne null quand le DTO est null |
| `toDTO_shouldHandleNullEntity()` | Vérifie que toDTO() retourne null quand l'entité est null |
| `toDTOList_shouldHandleNullList()` | Vérifie que toDTOList() retourne une liste vide pour une liste null |
| `toDTOList_shouldHandleEmptyList()` | Vérifie que toDTOList() retourne une liste vide pour une liste vide |
| `toDTOList_shouldHandleMultipleElements()` | Vérifie la conversion d'une liste avec plusieurs éléments |
| `toEntity_shouldHandleNullAdresse()` | Vérifie le mapping quand l'adresse du DTO est null |

---

### 10. PatientServiceTest (19 tests)

Tests du service `PatientService` qui contient la logique métier pour la gestion des patients.

#### Tests de Création
| Test | Description |
|------|-------------|
| `createPatient_shouldSaveAndReturnDTO()` | Vérifie qu'un patient est créé, sauvegardé et qu'un événement CREATED est enregistré |

#### Tests de Mise à Jour
| Test | Description |
|------|-------------|
| `updatePatient_shouldUpdateAndReturnDTO()` | Vérifie la mise à jour d'un patient existant et l'enregistrement d'un événement UPDATED |
| `updatePatient_shouldThrowWhenNotFound()` | Vérifie qu'une exception est levée si le patient n'existe pas |
| `updatePatient_appliesAllFieldChanges()` | Vérifie que tous les champs sont correctement mis à jour |

#### Tests de Suppression
| Test | Description |
|------|-------------|
| `deletePatient_shouldDelete()` | Vérifie la suppression d'un patient existant |
| `deletePatient_shouldThrowWhenNotFound()` | Vérifie qu'une exception est levée si le patient n'existe pas |

#### Tests de Récupération par ID
| Test | Description |
|------|-------------|
| `getPatientById_shouldReturnDTO()` | Vérifie la récupération d'un patient par son ID |
| `getPatientById_shouldThrowWhenNotFound()` | Vérifie qu'une exception est levée si le patient n'existe pas |

#### Tests de Récupération par CIN
| Test | Description |
|------|-------------|
| `getPatientByCin_shouldReturnDTO()` | Vérifie la récupération d'un patient par son numéro CIN |
| `getPatientByCin_shouldThrowWhenNotFound()` | Vérifie qu'une exception est levée si le CIN n'existe pas |

#### Tests de Recherche
| Test | Description |
|------|-------------|
| `searchPatients_withNom_shouldFilter()` | Vérifie la recherche par nom |
| `searchPatients_withCin_shouldFilter()` | Vérifie la recherche par CIN |
| `searchPatients_withNomAndCin_shouldFilter()` | Vérifie la recherche combinée nom + CIN |
| `searchPatients_withNoFilters_shouldReturnAll()` | Vérifie que sans filtre, tous les patients sont retournés |
| `searchPatients_withEmptyStrings_returnsAll()` | Vérifie que des chaînes vides retournent tous les patients |

#### Tests de Liste
| Test | Description |
|------|-------------|
| `listPatients_shouldReturnAllDTOs()` | Vérifie la récupération de tous les patients |
| `listPatients_returnsEmptyListWhenNoPatients()` | Vérifie le retour d'une liste vide quand il n'y a pas de patients |
| `listPatientsByCabinet_shouldReturnFilteredDTOs()` | Vérifie le filtrage par ID de cabinet |
| `listPatientsByCabinet_returnsEmptyListWhenNoMatch()` | Vérifie le retour d'une liste vide quand aucun patient ne correspond au cabinet |

---

### 11. PatientControllerTest (12 tests)

Tests du contrôleur REST `PatientController` qui expose les endpoints de l'API.

| Test | Endpoint | Description |
|------|----------|-------------|
| `createPatient_shouldReturnCreatedPatient()` | `POST /api/patients` | Vérifie la création d'un patient via l'API |
| `updatePatient_shouldReturnUpdatedPatient()` | `PUT /api/patients/{id}` | Vérifie la mise à jour d'un patient |
| `deletePatient_shouldCallService()` | `DELETE /api/patients/{id}` | Vérifie la suppression d'un patient |
| `getPatientById_shouldReturnPatient()` | `GET /api/patients/{id}` | Vérifie la récupération par ID |
| `getPatientByCin_shouldReturnPatient()` | `GET /api/patients/cin/{cin}` | Vérifie la récupération par CIN |
| `listPatients_noFilters_shouldReturnAll()` | `GET /api/patients` | Vérifie la liste sans filtres |
| `listPatients_withNom_shouldFilter()` | `GET /api/patients?nom=...` | Vérifie le filtrage par nom |
| `listPatients_withCin_shouldFilter()` | `GET /api/patients?cin=...` | Vérifie le filtrage par CIN |
| `listPatients_withNomAndCin_shouldFilter()` | `GET /api/patients?nom=...&cin=...` | Vérifie le filtrage combiné |
| `listPatientsByCabinet_shouldReturnFiltered()` | `GET /api/patients/by-cabinet/{cabinetId}` | Vérifie le filtrage par cabinet |
| `getPatientById_notFound_shouldThrow()` | `GET /api/patients/{id}` | Vérifie la gestion d'erreur 404 |
| `getPatientByCin_notFound_shouldThrow()` | `GET /api/patients/cin/{cin}` | Vérifie la gestion d'erreur 404 |

---

## ⚙️ Configuration de Test

### TestSecurityConfig

La classe `TestSecurityConfig` fournit une configuration de sécurité simplifiée pour les tests :
- Désactive la protection CSRF
- Configure une gestion de session stateless
- Autorise toutes les requêtes sans authentification

### application-test.yml

Le fichier de configuration de test désactive :
- Le client Config Server (`spring.cloud.config.enabled=false`)
- Le fail-fast du Config Server (`spring.cloud.config.fail-fast=false`)

---

## 🚀 Exécution des Tests

Pour exécuter tous les tests :

```bash
cd patient-service
mvn test
```

Pour exécuter un test spécifique :

```bash
mvn test -Dtest=PatientServiceTest
```

Pour générer un rapport de couverture (JaCoCo) :

```bash
mvn test jacoco:report
```

Le rapport sera disponible dans `target/site/jacoco/index.html`.

---

## 📊 Couverture de Code

Les tests couvrent les aspects suivants :

| Couche | Classes Testées | Couverture |
|--------|-----------------|------------|
| Entités | Patient, PatientEvent, Adresse | ✅ Complète |
| DTOs | PatientDTO | ✅ Complète |
| Enums | Sexe, TypeMutuelle, EventType | ✅ Complète |
| Mappers | PatientMapper | ✅ Complète |
| Services | PatientService | ✅ Complète |
| Controllers | PatientController | ✅ Complète |

---

## 📝 Notes Techniques

### Frameworks Utilisés
- **JUnit 5** : Framework de test principal
- **Mockito** : Mocking des dépendances
- **AssertJ** : Assertions fluides et lisibles

### Bonnes Pratiques Appliquées
1. **Isolation** : Chaque test est indépendant grâce au mocking
2. **Nomenclature claire** : `methodName_condition_expectedResult`
3. **Couverture des cas limites** : null, listes vides, entités non trouvées
4. **Tests des exceptions** : Vérification des RuntimeException avec messages appropriés

---

*Documentation générée le 14 janvier 2026*
