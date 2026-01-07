package com.gi.patientservice.web;

import com.gi.patientservice.dto.PatientDTO;
import com.gi.patientservice.service.PatientService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/patients")
@RequiredArgsConstructor
public class PatientController {

    private final PatientService patientService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PatientDTO createPatient(@RequestBody @Valid PatientDTO dto) {
        return patientService.createPatient(dto);
    }

    @PutMapping("/{id}")
    public PatientDTO updatePatient(@PathVariable Long id, @RequestBody @Valid PatientDTO dto) {
        return patientService.updatePatient(id, dto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletePatient(@PathVariable Long id) {
        patientService.deletePatient(id);
    }

    @GetMapping("/{id}")
    public PatientDTO getPatient(@PathVariable Long id) {
        return patientService.getPatientById(id);
    }

    @GetMapping("/cin/{cin}")
    public PatientDTO getByCin(@PathVariable String cin) {
        return patientService.getPatientByCin(cin);
    }

    @GetMapping
    public List<PatientDTO> listPatients(@RequestParam(required = false) String nom,
                                         @RequestParam(required = false) String prenom) {
        if (nom != null || prenom != null) {
            return patientService.searchPatients(nom, prenom);
        }
        return patientService.listPatients();
    }

    @GetMapping("/by-cabinet/{cabinetId}")
    public List<PatientDTO> listPatientsByCabinet(@PathVariable Long cabinetId) {
        return patientService.listPatientsByCabinet(cabinetId);
    }
}
