package com.gi.paymentservice.repository;

import com.gi.paymentservice.model.entity.BillingSubscription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BillingSubscriptionRepository extends JpaRepository<BillingSubscription, Long> {

    Optional<BillingSubscription> findByCabinetId(Long cabinetId);

    Optional<BillingSubscription> findByStripeSubscriptionId(String stripeSubscriptionId);
}
