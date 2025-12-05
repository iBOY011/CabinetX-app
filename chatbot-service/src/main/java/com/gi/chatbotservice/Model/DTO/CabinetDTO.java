package com.gi.chatbotservice.Model.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CabinetDTO {

    private Long id;

    private String nom;

    private String adresse;

    private String telephone;

    private List<MedecinDTO> medecins;
}
