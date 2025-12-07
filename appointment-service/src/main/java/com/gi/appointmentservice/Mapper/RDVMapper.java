package com.gi.appointmentservice.Mapper;

import org.springframework.stereotype.Component;

import com.gi.appointmentservice.Model.DTO.RDVRequest;
import com.gi.appointmentservice.Model.DTO.RDVResponse;
import com.gi.appointmentservice.Model.Entity.RendezVous;
import com.gi.appointmentservice.Model.Enum.StatutRDV;

@Component
public class RDVMapper {

    // Request → Entity (when creating RDV)
    public static RendezVous toEntity(RDVRequest dto) {
        RendezVous rdv = new RendezVous();
        rdv.setPatientId(dto.getPatientId());
        rdv.setCabinetId(dto.getCabinetId());
        rdv.setDate(dto.getDate());
        rdv.setHeure_debut(dto.getHeure_debut());
        rdv.setHeure_fin(dto.getHeure_fin());
        rdv.setMotifRDV(dto.getMotifRDV());
        rdv.setNotes(dto.getNotes());
        rdv.setStatutRDV(StatutRDV.CONFIRME);
        
        return rdv;
    }

    // Entity + patient names → Response
    public static RDVResponse toResponse(RendezVous rdv, String firstName, String lastName) {
        RDVResponse dto = new RDVResponse();
        dto.setId(rdv.getId());
        dto.setPatientId(rdv.getPatientId());
        dto.setPatientFirstName(firstName);
        dto.setPatientLastName(lastName);
        dto.setCabinetId(rdv.getCabinetId());
        dto.setDate(rdv.getDate());
        dto.setHeure_debut(rdv.getHeure_debut());
        dto.setHeure_fin(rdv.getHeure_fin());
        dto.setMotifRDV(rdv.getMotifRDV());
        dto.setNotes(rdv.getNotes());
        dto.setStatutRDV(rdv.getStatutRDV());
        return dto;
    }
}
