package com.gi.medicalrecordservice.model.entity;

import com.gi.medicalrecordservice.model.enums.DocumentType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

/**
 * Entité représentant un document médical attaché à un dossier patient.
 * 
 * <p>Stockée dans la table medical_document. Contient les documents binaires
 * (radiographies, analyses de laboratoire, ordonnances, etc.) associés
 * au dossier médical d'un patient.
 * 
 * <p>Informations stockées :
 * <ul>
 *   <li>fileName : Nom original du fichier uploadé</li>
 *   <li>fileType : Type MIME (ex: application/pdf, image/jpeg)</li>
 *   <li>fileUrl : URL externe (si stockage S3/Azure) - optionnel</li>
 *   <li>documentType : Catégorie (RADIOLOGY, LAB_RESULT, PRESCRIPTION, OTHER)</li>
 *   <li>content : Contenu binaire du fichier (BLOB)</li>
 *   <li>additionDate : Date d'ajout automatique</li>
 * </ul>
 * 
 * <p>Stratégie de stockage :
 * <ul>
 *   <li>Actuel : Stockage en base de données (champ BLOB content)</li>
 *   <li>Future : Migration possible vers S3/Azure Blob (fileUrl renseigné, content null)</li>
 * </ul>
 * 
 * <p>Relation ManyToOne avec MedicalRecord :
 * <ul>
 *   <li>FetchType LAZY : Chargé uniquement si accédé</li>
 *   <li>Cascade : Géré par le parent (MedicalRecord)</li>
 * </ul>
 * 
 * <p>Types de documents supportés (DocumentType) :
 * <ul>
 *   <li>RADIOLOGY : Radiographies, IRM, Scanner</li>
 *   <li>LAB_RESULT : Analyses de laboratoire (sang, urine, etc.)</li>
 *   <li>PRESCRIPTION : Ordonnances médicales</li>
 *   <li>OTHER : Autres documents (rapports, courriers, etc.)</li>
 * </ul>
 * 
 * @author CabinetX Development Team
 * @version 1.0
 * @since 2024-01
 */
@Entity
@Table(name = "medical_document")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = "medicalRecord")
public class MedicalDocument {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private MedicalRecord medicalRecord;

    @Column(nullable = false)
    private String fileName;

    private String fileType;

    private String fileUrl;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DocumentType documentType;

    @Lob
    @Column(nullable = false)
    private byte[] content;

    @Column(nullable = false)
    private LocalDateTime additionDate;

    @PrePersist
    void prePersist() {
        additionDate = LocalDateTime.now();
    }
}