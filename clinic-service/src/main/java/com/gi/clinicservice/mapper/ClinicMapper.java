package com.gi.clinicservice.mapper;

import com.gi.clinicservice.model.dto.request.ClinicRequest;
import com.gi.clinicservice.model.dto.response.ClinicResponse;
import com.gi.clinicservice.model.entity.Clinic;
import org.springframework.stereotype.Component;

@Component
public class ClinicMapper {

    public Clinic toEntity(ClinicRequest request) {
        if (request == null) return null;
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

    public ClinicResponse toResponse(Clinic entity) {
        if (entity == null) return null;
        ClinicResponse response = new ClinicResponse();
        response.setId(entity.getId());
        response.setName(entity.getName());
        response.setSpecialty(entity.getSpecialty());
        response.setPhone(entity.getPhone());
        response.setAddress(entity.getAddress());
        response.setLogoUrl(entity.getLogoUrl());
        response.setStatus(entity.getStatus());
        response.setServiceEndDate(entity.getServiceEndDate());
        return response;
    }

    public void updateEntity(ClinicRequest request, Clinic entity) {
        if (request == null || entity == null) return;
        entity.setName(request.getName());
        entity.setSpecialty(request.getSpecialty());
        entity.setPhone(request.getPhone());
        entity.setAddress(request.getAddress());
        entity.setLogoUrl(request.getLogoUrl());
        entity.setStatus(request.getStatus());
        entity.setServiceEndDate(request.getServiceEndDate());
    }
}