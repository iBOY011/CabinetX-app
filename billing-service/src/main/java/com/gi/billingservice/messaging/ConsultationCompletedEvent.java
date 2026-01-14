package com.gi.billingservice.messaging;

public class ConsultationCompletedEvent {
    private Long consultationId;
    private Long rendezVousId;
    private Long patientId;
    private Long medecinId;
    private Long cabinetId;
    private String diagnostic;
    private String traitement;
    private Double timestamp;

    // Default constructor
    public ConsultationCompletedEvent() {}

    // All-args constructor
    public ConsultationCompletedEvent(Long consultationId, Long rendezVousId, Long patientId, Long medecinId, Long cabinetId, String diagnostic, String traitement, Double timestamp) {
        this.consultationId = consultationId;
        this.rendezVousId = rendezVousId;
        this.patientId = patientId;
        this.medecinId = medecinId;
        this.cabinetId = cabinetId;
        this.diagnostic = diagnostic;
        this.traitement = traitement;
        this.timestamp = timestamp;
    }

    // Getters and setters
    public Long getConsultationId() {
        return consultationId;
    }

    public void setConsultationId(Long consultationId) {
        this.consultationId = consultationId;
    }

    public Long getRendezVousId() {
        return rendezVousId;
    }

    public void setRendezVousId(Long rendezVousId) {
        this.rendezVousId = rendezVousId;
    }

    public Long getPatientId() {
        return patientId;
    }

    public void setPatientId(Long patientId) {
        this.patientId = patientId;
    }

    public Long getMedecinId() {
        return medecinId;
    }

    public void setMedecinId(Long medecinId) {
        this.medecinId = medecinId;
    }

    public Long getCabinetId() {
        return cabinetId;
    }

    public void setCabinetId(Long cabinetId) {
        this.cabinetId = cabinetId;
    }

    public String getDiagnostic() {
        return diagnostic;
    }

    public void setDiagnostic(String diagnostic) {
        this.diagnostic = diagnostic;
    }

    public String getTraitement() {
        return traitement;
    }

    public void setTraitement(String traitement) {
        this.traitement = traitement;
    }

    public Double getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Double timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "ConsultationCompletedEvent{" +
                "consultationId=" + consultationId +
                ", rendezVousId=" + rendezVousId +
                ", patientId=" + patientId +
                ", medecinId=" + medecinId +
                ", cabinetId=" + cabinetId +
                ", diagnostic='" + diagnostic + '\'' +
                ", traitement='" + traitement + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ConsultationCompletedEvent that = (ConsultationCompletedEvent) o;
        return java.util.Objects.equals(consultationId, that.consultationId) &&
                java.util.Objects.equals(rendezVousId, that.rendezVousId) &&
                java.util.Objects.equals(patientId, that.patientId) &&
                java.util.Objects.equals(medecinId, that.medecinId) &&
                java.util.Objects.equals(cabinetId, that.cabinetId) &&
                java.util.Objects.equals(diagnostic, that.diagnostic) &&
                java.util.Objects.equals(traitement, that.traitement) &&
                java.util.Objects.equals(timestamp, that.timestamp);
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(consultationId, rendezVousId, patientId, medecinId, cabinetId, diagnostic, traitement, timestamp);
    }
}