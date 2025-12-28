package com.gi.medicalrecordservice.model.dto.response;

import com.gi.medicalrecordservice.model.enums.DocumentType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MedicalDocumentDTO {
    private Long id;
    private String fileName;
    private String fileType;
    private String fileUrl;
    private DocumentType documentType;
    private LocalDateTime additionDate;
}