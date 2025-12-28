package com.gi.paymentservice.repository;

import com.gi.paymentservice.model.entity.SubscriptionPayment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SubscriptionPaymentRepository extends JpaRepository<SubscriptionPayment, Long> {

    Optional<SubscriptionPayment> findByStripeSessionId(String sessionId);

    List<SubscriptionPayment> findByCabinetId(Long cabinetId);
}