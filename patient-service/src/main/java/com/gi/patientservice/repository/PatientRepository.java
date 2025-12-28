package com.gi.patientservice.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.gi.patientservice.entities.Patient;

@Repository
public interface PatientRepository extends JpaRepository<Patient, Long> {

		Optional<Patient> findByCin(String cin);

		List<Patient> findByNomContainingIgnoreCase(String nom);

		List<Patient> findByPrenomContainingIgnoreCase(String prenom);

		List<Patient> findByNomContainingIgnoreCaseAndPrenomContainingIgnoreCase(String nom, String prenom);
}
