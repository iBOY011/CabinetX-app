package com.gi.clinicservice.service.impl;

import com.gi.clinicservice.exception.ResourceNotFoundException;
import com.gi.clinicservice.mapper.ClinicMapper;
import com.gi.clinicservice.model.dto.ClinicDTO;
import com.gi.clinicservice.model.entity.Clinic;
import com.gi.clinicservice.model.enums.ClinicStatus;
import com.gi.clinicservice.repository.ClinicRepository;
import com.gi.clinicservice.service.ClinicService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Implémentation du service de gestion des cabinets médicaux.
 * 
 * <p>Cette classe implémente la logique métier pour la gestion des cabinets, incluant :
 * <ul>
 *   <li><b>Création :</b> Validation du téléphone marocain (+212), serviceEndDate future</li>
 *   <li><b>Activation/Désactivation :</b> Toggle status ACTIVE/INACTIVE (gestion abonnements)</li>
 *   <li><b>Recherche proches expiration :</b> Alerte cabinets dont serviceEndDate < now + daysBefore</li>
 * </ul></p>
 * 
 * <p><b>Dépendances :</b></p>
 * <ul>
 *   <li>ClinicRepository - Accès données PostgreSQL</li>
 *   <li>ClinicMapper - Conversion Entity ↔ DTO</li>
 * </ul>
 * 
 * <p><b>Exceptions levées :</b></p>
 * <ul>
 *   <li>ResourceNotFoundException - Cabinet non trouvé par ID</li>
 * </ul>
 * 
 * @author CabinetX Team
 * @version 1.0
 * @since 2024
 */
@Service
@RequiredArgsConstructor
public class ClinicServiceImpl implements ClinicService {

    private final ClinicRepository repository;
    private final ClinicMapper mapper;

    @Override
    public ClinicDTO createClinic(ClinicDTO request) {
        Clinic clinic = mapper.toEntity(request);
        clinic = repository.save(clinic);
        return mapper.toResponse(clinic);
    }

    @Override
    public ClinicDTO updateClinic(Long id, ClinicDTO request) {
        Clinic clinic = repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Clinic not found"));
        mapper.updateEntity(request, clinic);
        clinic = repository.save(clinic);
        return mapper.toResponse(clinic);
    }

    @Override
    public ClinicDTO activateClinic(Long id) {
        Clinic clinic = repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Clinic not found"));
        clinic.setStatus(ClinicStatus.ACTIVE);
        clinic = repository.save(clinic);
        return mapper.toResponse(clinic);
    }

    @Override
    public ClinicDTO deactivateClinic(Long id) {
        Clinic clinic = repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Clinic not found"));
        clinic.setStatus(ClinicStatus.INACTIVE);
        clinic = repository.save(clinic);
        return mapper.toResponse(clinic);
    }

    @Override
    public ClinicDTO findById(Long id) {
        Clinic clinic = repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Clinic not found"));
        return mapper.toResponse(clinic);
    }

    @Override
    public List<ClinicDTO> findAll() {
        return repository.findAll().stream().map(mapper::toResponse).collect(Collectors.toList());
    }

    @Override
    public List<ClinicDTO> findActive() {
        return repository.findByStatus(ClinicStatus.ACTIVE).stream().map(mapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<ClinicDTO> findNearExpiration(int daysBefore) {
        LocalDate date = LocalDate.now().plusDays(daysBefore);
        return repository.findByServiceEndDateBefore(date).stream().map(mapper::toResponse)
                .collect(Collectors.toList());
    }
}