package com.gi.appointmentservice.Model.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserInfoDTO {
    private Long id;
    private String keycloakUserId;
    private String firstName;
    private String lastName;
    private String login;
    private String phoneNumber;
    private String role;
    private Long clinicId;
    private boolean active;
}
