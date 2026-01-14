package com.gi.patientservice.enums;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;

class SexeTest {

    @Test
    void values_containsAllExpectedValues() {
        assertThat(Sexe.values()).containsExactly(
                Sexe.MASCULIN,
                Sexe.FEMININ,
                Sexe.AUTRE
        );
    }

    @Test
    void valueOf_returnsCorrectEnum() {
        assertThat(Sexe.valueOf("MASCULIN")).isEqualTo(Sexe.MASCULIN);
        assertThat(Sexe.valueOf("FEMININ")).isEqualTo(Sexe.FEMININ);
        assertThat(Sexe.valueOf("AUTRE")).isEqualTo(Sexe.AUTRE);
    }

    @Test
    void name_returnsString() {
        assertThat(Sexe.MASCULIN.name()).isEqualTo("MASCULIN");
        assertThat(Sexe.FEMININ.name()).isEqualTo("FEMININ");
        assertThat(Sexe.AUTRE.name()).isEqualTo("AUTRE");
    }

    @Test
    void ordinal_returnsCorrectIndex() {
        assertThat(Sexe.MASCULIN.ordinal()).isEqualTo(0);
        assertThat(Sexe.FEMININ.ordinal()).isEqualTo(1);
        assertThat(Sexe.AUTRE.ordinal()).isEqualTo(2);
    }
}
