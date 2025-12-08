package com.gi.clinicservice.mapper;

import com.gi.clinicservice.model.dto.ClinicDTO;
import com.gi.clinicservice.model.entity.Clinic;
import org.springframework.stereotype.Component;

@Component
public class ClinicMapper {

    public Clinic toEntity(ClinicDTO request) {
        if (request == null)
            return null;
        Clinic clinic = new Clinic();
        clinic.setName(request.getName());
        clinic.setSpecialty(request.getSpecialty());
        clinic.setPhone(request.getPhone());
        clinic.setAddress(request.getAddress());
        clinic.setLogoUrl(request.getLogoUrl());
        clinic.setStatus(request.getStatus());
        clinic.setServiceEndDate(request.getServiceEndDate());
        return clinic;
    }

    public ClinicDTO toResponse(Clinic entity) {
        if (entity == null)
            return null;
        ClinicDTO response = new ClinicDTO();
        response.setName(entity.getName());
        response.setSpecialty(entity.getSpecialty());
        response.setPhone(entity.getPhone());
        response.setAddress(entity.getAddress());
        response.setLogoUrl(entity.getLogoUrl());
        response.setStatus(entity.getStatus());
        response.setServiceEndDate(entity.getServiceEndDate());
        return response;
    }

    public void updateEntity(ClinicDTO request, Clinic entity) {
        if (request == null || entity == null)
            return;
        entity.setName(request.getName());
        entity.setSpecialty(request.getSpecialty());
        entity.setPhone(request.getPhone());
        entity.setAddress(request.getAddress());
        entity.setLogoUrl(request.getLogoUrl());
        entity.setStatus(request.getStatus());
        entity.setServiceEndDate(request.getServiceEndDate());
    }
}