package com.gi.paymentservice.repository;

import com.gi.paymentservice.model.entity.BillingCustomer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BillingCustomerRepository extends JpaRepository<BillingCustomer, Long> {

    Optional<BillingCustomer> findByCabinetId(Long cabinetId);

    Optional<BillingCustomer> findByStripeCustomerId(String stripeCustomerId);
}
