package com.gi.patientservice.enums;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;

class TypeMutuelleTest {

    @Test
    void values_containsAllExpectedValues() {
        assertThat(TypeMutuelle.values()).containsExactly(
                TypeMutuelle.AUCUNE,
                TypeMutuelle.CNSS,
                TypeMutuelle.CNOPS,
                TypeMutuelle.PRIVEE
        );
    }

    @Test
    void valueOf_returnsCorrectEnum() {
        assertThat(TypeMutuelle.valueOf("AUCUNE")).isEqualTo(TypeMutuelle.AUCUNE);
        assertThat(TypeMutuelle.valueOf("CNSS")).isEqualTo(TypeMutuelle.CNSS);
        assertThat(TypeMutuelle.valueOf("CNOPS")).isEqualTo(TypeMutuelle.CNOPS);
        assertThat(TypeMutuelle.valueOf("PRIVEE")).isEqualTo(TypeMutuelle.PRIVEE);
    }

    @Test
    void name_returnsString() {
        assertThat(TypeMutuelle.AUCUNE.name()).isEqualTo("AUCUNE");
        assertThat(TypeMutuelle.CNSS.name()).isEqualTo("CNSS");
        assertThat(TypeMutuelle.CNOPS.name()).isEqualTo("CNOPS");
        assertThat(TypeMutuelle.PRIVEE.name()).isEqualTo("PRIVEE");
    }

    @Test
    void ordinal_returnsCorrectIndex() {
        assertThat(TypeMutuelle.AUCUNE.ordinal()).isEqualTo(0);
        assertThat(TypeMutuelle.CNSS.ordinal()).isEqualTo(1);
        assertThat(TypeMutuelle.CNOPS.ordinal()).isEqualTo(2);
        assertThat(TypeMutuelle.PRIVEE.ordinal()).isEqualTo(3);
    }
}
