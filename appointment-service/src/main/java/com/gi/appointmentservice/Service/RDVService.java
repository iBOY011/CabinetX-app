package com.gi.appointmentservice.Service;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import com.gi.appointmentservice.Mapper.RDVMapper;
import com.gi.appointmentservice.Model.DTO.PatientInfoDTO;
import com.gi.appointmentservice.Model.DTO.RDVRequest;
import com.gi.appointmentservice.Model.DTO.RDVResponse;
import com.gi.appointmentservice.Model.DTO.UpdateDto;
import com.gi.appointmentservice.Model.Entity.RendezVous;
import com.gi.appointmentservice.Repository.RDVRepository;

import lombok.RequiredArgsConstructor;



@Service
@RequiredArgsConstructor

public class RDVService {
    private  final RDVRepository rdvRepository;
    private  final RDVMapper RDVMapper;
    private final PatientClient patientClient;
    private PatientInfoDTO patientInfo;
    

    public RDVResponse createRendezVous(RDVRequest rdvreqDto) {

        // 1. date début < date fin
        if(!rdvreqDto.getHeure_debut().isBefore(rdvreqDto.getHeure_fin())){
            throw new IllegalArgumentException("La date de début doit être avant la date de fin.");
        }
        // 2. vérifier qu'il n'existe pas un rdv dans le même cabinet qui chevauche l'horaire
        
        RendezVous rdv = new RendezVous();
        rdv= RDVMapper.toEntity(rdvreqDto);
        rdvRepository.save(rdv);
        patientInfo = patientClient.getPatientById(rdv.getPatientId());
        return RDVMapper.toResponse(rdv, patientInfo);

    }

    public RDVResponse updateRendezVous(Long rdvId, UpdateDto updateDto) {
        RendezVous rdv = rdvRepository.findById(rdvId)
                .orElseThrow(() -> new ResourceNotFoundException("RendezVous not found with id: " + rdvId));
        rdv.setDate(updateDto.getDate());
        rdv.setHeure_debut(updateDto.getHeure_debut());
        rdv.setHeure_fin(updateDto.getHeure_fin());
        rdv.setMotifRDV(updateDto.getMotifRDV());
        rdv.setNotes(updateDto.getNotes());
        rdvRepository.save(rdv);

        patientInfo = patientClient.getPatientById(rdv.getPatientId());
        return RDVMapper.toResponse(rdv, patientInfo);
    }

    public void deleteRendezVous(Long rdvId) {
        rdvRepository.deleteById(rdvId);
    }

    public RDVResponse getRendezVousById(Long rdvId) {
        RendezVous rdv = rdvRepository.findById(rdvId)
                .orElseThrow(() -> new ResourceNotFoundException("RendezVous not found with id: " + rdvId));
        patientInfo = patientClient.getPatientById(rdv.getPatientId());
        return RDVMapper.toResponse(rdv, patientInfo); // here we should retrieve actual patient names from Patient Service
    }

}
