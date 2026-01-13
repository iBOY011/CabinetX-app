package com.gi.consultationservice.web;

import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;
import org.springframework.web.server.ResponseStatusException;

import com.gi.consultationservice.dto.ConsultationDTO;
import com.gi.consultationservice.dto.ConsultationSummaryDTO;
import com.gi.consultationservice.enums.ConsultationType;
import com.gi.consultationservice.service.ConsultationService;

import graphql.GraphQLException;

@Controller
public class ConsultationGraphQLConnector {

	private static final int DEFAULT_LIMIT = 10;

	private final ConsultationService consultationService;

	public ConsultationGraphQLConnector(ConsultationService consultationService) {
		this.consultationService = consultationService;
	}

	@QueryMapping
	public ConsultationDTO consultationById(@Argument Long id) {
		return fetchConsultation(id);
	}

	@QueryMapping
	public List<ConsultationDTO> consultationsByPatient(@Argument Long patientId,
														@Argument Integer limit) {
		return withGraphQlErrors(() -> {
		int resolvedLimit = (limit == null || limit <= 0) ? DEFAULT_LIMIT : limit;
		List<ConsultationSummaryDTO> summaries = consultationService.listerParPatient(patientId);
		return hydrateSummaries(summaries, resolvedLimit);
		});
	}

	@QueryMapping
	public List<ConsultationDTO> medecinConsultationsWindow(@Argument Long medecinId,
															@Argument ConsultationWindowInput window) {
		return withGraphQlErrors(() -> {
			if (window == null || window.from() == null || window.to() == null) {
				throw new GraphQLException("Fenetre de consultation invalide");
			}
		List<ConsultationSummaryDTO> summaries = consultationService.listerParMedecinEtPeriode(
				medecinId,
				window.from(),
				window.to()
		);
		return hydrateSummaries(summaries, Integer.MAX_VALUE);
		});
	}

	@MutationMapping
	public ConsultationDTO scheduleConsultation(@Argument ConsultationScheduleInput input) {
		return withGraphQlErrors(() -> consultationService.creerConsultation(input.toDto()));
	}

	@MutationMapping
	public ConsultationDTO updateConsultationNotes(@Argument Long id,
												   @Argument ConsultationNotesInput notes) {
		return withGraphQlErrors(() -> {
			ConsultationDTO existing = fetchConsultation(id);
			applyNotes(existing, notes);
			return consultationService.modifierConsultation(id, existing);
		});
	}

	private List<ConsultationDTO> hydrateSummaries(List<ConsultationSummaryDTO> summaries, int limit) {
		if (summaries == null || summaries.isEmpty()) {
			return Collections.emptyList();
		}
		return summaries.stream()
				.limit(limit)
				.map(summary -> fetchConsultation(summary.getId()))
				.collect(Collectors.toList());
	}

	private ConsultationDTO fetchConsultation(Long id) {
		return withGraphQlErrors(() -> consultationService.trouverParId(id));
	}

	private <T> T withGraphQlErrors(Supplier<T> supplier) {
		try {
			return supplier.get();
		} catch (ResponseStatusException ex) {
			String message = ex.getReason() != null ? ex.getReason() : "Consultation introuvable";
			throw new GraphQLException(message, ex);
		}
	}
	private void applyNotes(ConsultationDTO target, ConsultationNotesInput notes) {
		if (notes.examenClinique() != null) {
			target.setExamenClinique(notes.examenClinique());
		}
		if (notes.examenSupplementaire() != null) {
			target.setExamenSupplementaire(notes.examenSupplementaire());
		}
		if (notes.diagnostic() != null) {
			target.setDiagnostic(notes.diagnostic());
		}
		if (notes.traitement() != null) {
			target.setTraitement(notes.traitement());
		}
		if (notes.observations() != null) {
			target.setObservations(notes.observations());
		}
	}

	public record ConsultationScheduleInput(Long rendezVousId,
												 Long patientId,
												 Long medecinId,
												 Long cabinetId,
												 ConsultationType type,
												 OffsetDateTime dateConsultation,
											 String examenClinique,
											 String examenSupplementaire,
											 String diagnostic,
											 String traitement,
											 String observations) {

		ConsultationDTO toDto() {
			return ConsultationDTO.builder()
					.rendezVousId(rendezVousId)
					.patientId(patientId)
					.medecinId(medecinId)
					.cabinetId(cabinetId)
					.type(type)
					.dateConsultation(dateConsultation)
					.examenClinique(examenClinique)
					.examenSupplementaire(examenSupplementaire)
					.diagnostic(diagnostic)
					.traitement(traitement)
					.observations(observations)
					.build();
		}
	}

	public record ConsultationNotesInput(String examenClinique,
										 String examenSupplementaire,
										 String diagnostic,
										 String traitement,
										 String observations) {
	}

	public record ConsultationWindowInput(OffsetDateTime from, OffsetDateTime to) {
	}
}
