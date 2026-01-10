# Tests unitaires et d'intégration

Ce document récapitule les tests écrits et exécutés dans `prescription-service`.

## Mapper
- **PrescriptionMapperTest** : vérifie le mapping complet entre `Prescription` et `PrescriptionDTO`, y compris les champs `digitalSignature`, les dates, les lignes et le type `PrescriptionLineType`.

## Service
- **PrescriptionServiceImplTest** :
  - création d'une prescription : persistance des lignes et de `digitalSignature` en utilisant les dépôts mockés.
  - récupération par identifiant : lève une exception si l'entité est absente ; retourne la prescription sinon.
  - mise à jour : met à jour les champs principaux et `digitalSignature`, avec contrôle d'existence.

## Contexte Spring Boot
- **PrescriptionServiceApplicationTests** : charge le contexte Spring Boot (profil `test`) pour vérifier la configuration de base sans dépendances externes.

## Notes
- Les tests sont exécutés avec JUnit 5 et Mockito (mock-maker inline).
- Le profil `test` utilise une configuration de sécurité simplifiée pour éviter la dépendance à un `JwtDecoder`.
- Un H2 en mémoire est utilisé pendant les tests ; aucune ressource externe n'est requise.
