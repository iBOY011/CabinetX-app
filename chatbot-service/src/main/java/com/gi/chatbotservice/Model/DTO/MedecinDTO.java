package com.gi.chatbotservice.Model.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MedecinDTO {

    private Long id;

    private String nom;

    private String prenom;

    private String specialite;

    private Long cabinetId;
}
