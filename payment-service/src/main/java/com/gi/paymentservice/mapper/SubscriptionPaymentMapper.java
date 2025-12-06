package com.gi.paymentservice.mapper;

import com.gi.paymentservice.model.dto.request.PaymentRequestDTO;
import com.gi.paymentservice.model.dto.response.SubscriptionPaymentDTO;
import com.gi.paymentservice.model.entity.SubscriptionPayment;
import com.gi.paymentservice.model.enums.PaymentStatus;
import com.gi.paymentservice.model.enums.PaymentType;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class SubscriptionPaymentMapper {

    public SubscriptionPayment toEntity(PaymentRequestDTO dto) {
        SubscriptionPayment payment = new SubscriptionPayment();
        payment.setSubscriptionId(dto.getSubscriptionId());
        payment.setCabinetId(dto.getCabinetId());
        payment.setDoctorId(dto.getDoctorId());
        payment.setAmount(dto.getAmount());
        payment.setCurrency("EUR"); // default
        payment.setPaymentType(PaymentType.SUBSCRIPTION_CABINET);
        payment.setStatus(PaymentStatus.INITIATED);
        payment.setCreatedAt(LocalDateTime.now());
        return payment;
    }

    public SubscriptionPaymentDTO toDTO(SubscriptionPayment entity) {
        return new SubscriptionPaymentDTO(
            entity.getId(),
            entity.getSubscriptionId(),
            entity.getCabinetId(),
            entity.getAmount(),
            entity.getCurrency(),
            entity.getStatus(),
            entity.getRedirectUrl(),
            entity.getCreatedAt(),
            entity.getPaidAt()
        );
    }
}