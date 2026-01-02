package com.gi.appointmentservice.Repository;
import java.time.LocalDate;
import java.util.Collection;
import java.util.Optional;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.gi.appointmentservice.Model.Entity.RendezVous;
import com.gi.appointmentservice.Model.Enum.StatutRDV;

@Repository
public interface RDVRepository extends JpaRepository<RendezVous, Long> {
	@Query("select r from RendezVous r where r.patientId = :patientId and r.date = :date and r.statutRDV in :statuts order by r.Heure_debut asc")
	Optional<RendezVous> findFirstValidToday(@Param("patientId") Long patientId, @Param("date") LocalDate date, @Param("statuts") Collection<StatutRDV> statuts);

    List<RendezVous> findByDateAndCabinetId(LocalDate date, Long cabinetId);

    // Queue management queries
    List<RendezVous> findByCabinetIdAndDateAndStatutRDV(Long cabinetId, LocalDate date, StatutRDV statutRDV);

    @Query("SELECT MAX(r.queuePosition) FROM RendezVous r WHERE r.cabinetId = :cabinetId AND r.date = :date AND r.statutRDV = 'EN_ATTENTE'")
    Integer findMaxQueuePositionByCabinetAndDate(@Param("cabinetId") Long cabinetId, @Param("date") LocalDate date);

}
