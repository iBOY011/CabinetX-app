package com.gi.patientservice.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.gi.patientservice.entities.Patient;

/**
 * Repository pour la gestion de la persistence des entités Patient.
 * 
 * <p>Fournit des méthodes de recherche spécialisées pour les patients, incluant :
 * <ul>
 *   <li>Recherche par CIN (unique identifiant marocain)</li>
 *   <li>Recherche par nom et/ou prénom (insensible à la casse)</li>
 *   <li>Filtrage par cabinet médical</li>
 * </ul>
 * 
 * <p>Toutes les méthodes de recherche textuelles sont insensibles à la casse pour
 * améliorer l'expérience utilisateur lors des recherches.
 * 
 * @author CabinetX Development Team
 * @version 1.0
 * @since 2024-01
 */
@Repository
public interface PatientRepository extends JpaRepository<Patient, Long> {

	/**
	 * Recherche un patient par son CIN.
	 * 
	 * @param cin le numéro CIN du patient (format: AB123456)
	 * @return Optional contenant le patient si trouvé
	 */
	Optional<Patient> findByCin(String cin);

	/**
	 * Recherche les patients dont le nom contient la chaîne spécifiée (insensible à la casse).
	 * 
	 * @param nom chaîne à rechercher dans les noms
	 * @return liste des patients correspondants
	 */
	List<Patient> findByNomContainingIgnoreCase(String nom);

	/**
	 * Recherche les patients dont le prénom contient la chaîne spécifiée (insensible à la casse).
	 * 
	 * @param prenom chaîne à rechercher dans les prénoms
	 * @return liste des patients correspondants
	 */
	List<Patient> findByPrenomContainingIgnoreCase(String prenom);

	/**
	 * Recherche les patients par nom ET prénom (insensible à la casse).
	 * 
	 * @param nom chaîne à rechercher dans les noms
	 * @param prenom chaîne à rechercher dans les prénoms
	 * @return liste des patients correspondants aux deux critères
	 */
	List<Patient> findByNomContainingIgnoreCaseAndPrenomContainingIgnoreCase(String nom, String prenom);

	/**
	 * Récupère tous les patients d'un cabinet médical spécifique.
	 * 
	 * @param cabinetId identifiant du cabinet
	 * @return liste des patients du cabinet
	 */
		List<Patient> findByCabinetId(Long cabinetId);
}
