package com.gi.appointmentservice.Repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.gi.appointmentservice.Model.Entity.RendezVous;


@Repository
public interface RDVRepository extends JpaRepository<RendezVous, Long> {
    
}
