package com.gi.patientservice.web;

import com.gi.patientservice.dto.PatientDTO;
import com.gi.patientservice.service.PatientService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST API Controller for patient management operations.
 * 
 * <p>This controller exposes HTTP endpoints for all patient-related operations
 * including CRUD operations, search functionality, and clinic-based filtering.
 * All endpoints are secured via Spring Security with OAuth2/JWT authentication.</p>
 * 
 * <p><b>Base Path:</b> {@code /api/patients}</p>
 * 
 * <p><b>Security:</b> All endpoints require authentication. Role-based access:
 * <ul>
 *   <li>ROLE_DOCTOR: Full access</li>
 *   <li>ROLE_SECRETARY: Full access</li>
 *   <li>ROLE_PATIENT: Read-only access to own record</li>
 * </ul>
 * 
 * <p><b>Response Formats:</b> All responses are in JSON format</p>
 * <p><b>Error Handling:</b> Uses Spring's @ExceptionHandler for consistent error responses</p>
 * 
 * @author CabinetX Development Team
 * @version 1.0
 * @since 2025
 * @see PatientService
 * @see PatientDTO
 */
@RestController
@RequestMapping("/api/patients")
@RequiredArgsConstructor
public class PatientController {

    private final PatientService patientService;

    /**
     * Creates a new patient record.
     * 
     * <p><b>HTTP Method:</b> POST</p>
     * <p><b>Endpoint:</b> {@code /api/patients}</p>
     * <p><b>Request Body:</b> PatientDTO in JSON format</p>
     * <p><b>Success Response:</b> 201 CREATED with created patient data</p>
     * 
     * @param dto Patient data with all required fields (cin, nom, prenom, etc.)
     * @return Created patient with generated ID
     * @throws org.springframework.web.bind.MethodArgumentNotValidException if validation fails
     * @throws org.springframework.dao.DataIntegrityViolationException if CIN already exists
     */
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

    /**
     * Lists all patients with optional search filtering.
     * 
     * <p><b>HTTP Method:</b> GET</p>
     * <p><b>Endpoint:</b> {@code /api/patients}</p>
     * <p><b>Query Parameters:</b></p>
     * <ul>
     *   <li>{@code nom} - Filter by family name (partial match, case-insensitive)</li>
     *   <li>{@code prenom} - Filter by first name (partial match, case-insensitive)</li>
     * </ul>
     * <p><b>Examples:</b></p>
     * <ul>
     *   <li>{@code GET /api/patients} - Returns all patients</li>
     *   <li>{@code GET /api/patients?nom=alami} - Search by family name</li>
     *   <li>{@code GET /api/patients?nom=alami&prenom=fatima} - Combined search</li>
     * </ul>
     *
     * @param nom Optional family name filter (partial, case-insensitive)
     * @param prenom Optional first name filter (partial, case-insensitive)
     * @return List of patients matching criteria, empty list if none found
     */
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
