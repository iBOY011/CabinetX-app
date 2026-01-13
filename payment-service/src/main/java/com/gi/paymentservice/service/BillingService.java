package com.gi.paymentservice.service;

import java.util.List;

import com.gi.paymentservice.model.dto.request.BillingCustomerRequest;
import com.gi.paymentservice.model.dto.request.CancelSubscriptionRequest;
import com.gi.paymentservice.model.dto.request.CheckoutSessionRequest;
import com.gi.paymentservice.model.dto.response.BillingCustomerDTO;
import com.gi.paymentservice.model.dto.response.BillingPlanDTO;
import com.gi.paymentservice.model.dto.response.BillingSubscriptionDTO;
import com.gi.paymentservice.model.dto.response.CheckoutSessionResponse;
import com.gi.paymentservice.model.dto.response.SubscriptionPaymentDTO;

public interface BillingService {

    CheckoutSessionResponse createCheckoutSession(CheckoutSessionRequest request);

    BillingCustomerDTO getOrCreateCustomer(BillingCustomerRequest request);

    List<BillingPlanDTO> listPlans();

    BillingSubscriptionDTO getSubscription(Long cabinetId);

    List<SubscriptionPaymentDTO> getInvoices(Long cabinetId);

    BillingSubscriptionDTO cancelSubscription(CancelSubscriptionRequest request);

    void handleStripeWebhook(String payload, String signature);

    void purgeSubscription(Long cabinetId);
}
