package com.gi.medicalrecordservice.dto;

import com.gi.medicalrecordservice.enums.TypeDocument;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DocumentMedicalDTO {
    private Long id;
    private String nomFichier;
    private String typeFichier;
    private String urlFichier;
    private TypeDocument typeDocument;
    private LocalDateTime dateAjout;
}
