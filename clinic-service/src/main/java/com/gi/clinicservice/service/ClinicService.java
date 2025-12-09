package com.gi.clinicservice.service;

import com.gi.clinicservice.model.dto.ClinicDTO;

import java.util.List;

public interface ClinicService {
    ClinicDTO createClinic(ClinicDTO request);
    ClinicDTO updateClinic(Long id, ClinicDTO request);
    ClinicDTO activateClinic(Long id);
    ClinicDTO deactivateClinic(Long id);
    ClinicDTO findById(Long id);
    List<ClinicDTO> findAll();
    List<ClinicDTO> findActive();
    List<ClinicDTO> findNearExpiration(int daysBefore);
}