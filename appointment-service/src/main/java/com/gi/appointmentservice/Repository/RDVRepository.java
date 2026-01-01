package com.gi.appointmentservice.Repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.gi.appointmentservice.Model.Entity.RendezVous;

@Repository
public interface RDVRepository extends JpaRepository<RendezVous, Long> {

    List<RendezVous> findByDateAndCabinetId(LocalDate date, Long cabinetId);

}
