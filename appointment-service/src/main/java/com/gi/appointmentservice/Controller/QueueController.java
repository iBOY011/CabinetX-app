package com.gi.appointmentservice.Controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.gi.appointmentservice.Model.DTO.RDVResponse;
import com.gi.appointmentservice.Service.QueueService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/queue")
public class QueueController {

    private final QueueService queueService;

    /**
     * Get all appointments in queue for a specific date and cabinet
     * GET /api/queue/by-date?date=2026-01-02&cabinetId=1
     */
    @GetMapping("/by-date")
    public ResponseEntity<List<RDVResponse>> getQueueByDate(
            @RequestParam LocalDate date,
            @RequestParam Long cabinetId) {
        List<RDVResponse> queue = queueService.getQueueByDateAndCabinet(date, cabinetId);
        return ResponseEntity.ok(queue);
    }

    /**
     * Add an appointment to the queue
     * PUT /api/queue/add/{appointmentId}
     */
    @PutMapping("/add/{appointmentId}")
    public ResponseEntity<RDVResponse> addToQueue(@PathVariable Long appointmentId) {
        RDVResponse response = queueService.addToQueue(appointmentId);
        return ResponseEntity.ok(response);
    }

    /**
     * Remove an appointment from the queue (back to CONFIRME status)
     * PUT /api/queue/remove/{appointmentId}
     */
    @PutMapping("/remove/{appointmentId}")
    public ResponseEntity<RDVResponse> removeFromQueue(@PathVariable Long appointmentId) {
        RDVResponse response = queueService.removeFromQueue(appointmentId);
        return ResponseEntity.ok(response);
    }

    /**
     * Call next patient (move to EN_CONSULTATION)
     * PUT /api/queue/call-next/{appointmentId}
     */
    @PutMapping("/call-next/{appointmentId}")
    public ResponseEntity<RDVResponse> callNext(@PathVariable Long appointmentId) {
        RDVResponse response = queueService.callNext(appointmentId);
        return ResponseEntity.ok(response);
    }

    /**
     * Reorder queue (for drag & drop)
     * POST /api/queue/reorder
     * Body: { "cabinetId": 1, "date": "2026-01-02", "appointmentIds": [3, 1, 2] }
     */
    @PostMapping("/reorder")
    public ResponseEntity<List<RDVResponse>> reorderQueue(@RequestBody ReorderQueueRequest request) {
        List<RDVResponse> updatedQueue = queueService.reorderQueue(
                request.getCabinetId(),
                request.getDate(),
                request.getAppointmentIds()
        );
        return ResponseEntity.ok(updatedQueue);
    }

    // DTO for reorder request
    public static class ReorderQueueRequest {
        private Long cabinetId;
        private LocalDate date;
        private List<Long> appointmentIds;

        public Long getCabinetId() {
            return cabinetId;
        }

        public void setCabinetId(Long cabinetId) {
            this.cabinetId = cabinetId;
        }

        public LocalDate getDate() {
            return date;
        }

        public void setDate(LocalDate date) {
            this.date = date;
        }

        public List<Long> getAppointmentIds() {
            return appointmentIds;
        }

        public void setAppointmentIds(List<Long> appointmentIds) {
            this.appointmentIds = appointmentIds;
        }
    }
}
