package com.gi.patientservice.entities;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;

class AdresseTest {

    @Test
    void builder_createsAdresseWithAllFields() {
        Adresse adresse = Adresse.builder()
                .rue("25 Rue Hassan II")
                .ville("Rabat")
                .codePostal("10000")
                .pays("Maroc")
                .build();

        assertThat(adresse.getRue()).isEqualTo("25 Rue Hassan II");
        assertThat(adresse.getVille()).isEqualTo("Rabat");
        assertThat(adresse.getCodePostal()).isEqualTo("10000");
        assertThat(adresse.getPays()).isEqualTo("Maroc");
    }

    @Test
    void setters_modifyFields() {
        Adresse adresse = new Adresse();
        adresse.setRue("10 Boulevard Mohammed V");
        adresse.setVille("Fès");
        adresse.setCodePostal("30000");
        adresse.setPays("Maroc");

        assertThat(adresse.getRue()).isEqualTo("10 Boulevard Mohammed V");
        assertThat(adresse.getVille()).isEqualTo("Fès");
        assertThat(adresse.getCodePostal()).isEqualTo("30000");
        assertThat(adresse.getPays()).isEqualTo("Maroc");
    }

    @Test
    void noArgsConstructor_createsEmptyAdresse() {
        Adresse adresse = new Adresse();

        assertThat(adresse.getRue()).isNull();
        assertThat(adresse.getVille()).isNull();
        assertThat(adresse.getCodePostal()).isNull();
        assertThat(adresse.getPays()).isNull();
    }

    @Test
    void allArgsConstructor_createsAdresse() {
        Adresse adresse = new Adresse("Rue A", "Ville B", "12345", "Pays C");

        assertThat(adresse.getRue()).isEqualTo("Rue A");
        assertThat(adresse.getVille()).isEqualTo("Ville B");
        assertThat(adresse.getCodePostal()).isEqualTo("12345");
        assertThat(adresse.getPays()).isEqualTo("Pays C");
    }

    @Test
    void toString_containsAllFields() {
        Adresse adresse = Adresse.builder()
                .rue("Rue Test")
                .ville("Ville Test")
                .codePostal("00000")
                .pays("Pays Test")
                .build();

        String str = adresse.toString();

        assertThat(str).contains("Rue Test");
        assertThat(str).contains("Ville Test");
        assertThat(str).contains("00000");
        assertThat(str).contains("Pays Test");
    }
}
