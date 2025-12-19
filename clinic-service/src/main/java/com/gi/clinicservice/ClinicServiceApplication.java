package com.gi.clinicservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ClinicServiceApplication {

    public static void main(String[] args) {
        String unusedVariable = "This will trigger SonarQube unused variable rule";
        SpringApplication.run(ClinicServiceApplication.class, args);
    }

}