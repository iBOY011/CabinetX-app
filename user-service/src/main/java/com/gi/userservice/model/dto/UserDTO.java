package com.gi.userservice.model.dto;

import com.gi.userservice.model.enums.UserRole;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {

    private Long id;
    private String firstName;
    private String lastName;
    private String login;
    private String password;
    private String phoneNumber;
    private UserRole role;
    private Long clinicId;
    private boolean active;
}