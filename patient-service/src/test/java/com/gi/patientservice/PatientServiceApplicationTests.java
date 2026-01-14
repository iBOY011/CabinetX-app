package com.gi.patientservice;

import com.gi.patientservice.security.TestSecurityConfig;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@ActiveProfiles("test")
@Import(TestSecurityConfig.class)
@TestPropertySource(properties = {
    "spring.cloud.config.enabled=false",
    "spring.cloud.config.fail-fast=false",
    "spring.config.import=",
    "spring.main.allow-bean-definition-overriding=true"
})
class PatientServiceApplicationTests {

    @Test
    void contextLoads() {
    }

}
