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