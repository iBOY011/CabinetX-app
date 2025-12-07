package com.gi.clinicservice.service;

import com.gi.clinicservice.model.dto.request.ClinicRequest;
import com.gi.clinicservice.model.dto.response.ClinicResponse;

import java.util.List;

public interface ClinicService {
    ClinicResponse createClinic(ClinicRequest request);
    ClinicResponse updateClinic(Long id, ClinicRequest request);
    ClinicResponse activateClinic(Long id);
    ClinicResponse deactivateClinic(Long id);
    ClinicResponse findById(Long id);
    List<ClinicResponse> findAll();
    List<ClinicResponse> findActive();
    List<ClinicResponse> findNearExpiration(int daysBefore);
}