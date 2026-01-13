package com.gi.paymentservice.config;

import java.util.HashMap;
import java.util.Map;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Data;

@Configuration
@ConfigurationProperties(prefix = "stripe")
@Data
public class StripeProperties {

    /** Secret key used to sign Stripe API requests. */
    private String secretKey;

    /** Webhook signing secret used to validate incoming webhooks. */
    private String webhookSecret;

    /** Optional default success URL template. */
    private String successUrl;

    /** Optional default cancel URL template. */
    private String cancelUrl;

    /** Configured plans keyed by a friendly plan id (e.g. basic, pro). */
    private Map<String, Plan> plans = new HashMap<>();

    @Data
    public static class Plan {
        private String priceId;
        private String name;
        private Long amount;
        private String currency;
        private String interval;
        private String description;
    }
}
