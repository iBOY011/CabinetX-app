package com.gi.userservice.service;

import java.util.List;

import com.gi.userservice.model.dto.UserDTO;
import com.gi.userservice.model.dto.request.CreateUserRequest;
import com.gi.userservice.model.enums.UserRole;

public interface UserService {

    UserDTO createUser(CreateUserRequest request);

    UserDTO findById(Long id);

    List<UserDTO> findAll();

    UserDTO updateUser(Long id, UserDTO dto);

    UserDTO activateUser(Long id);

    UserDTO deactivateUser(Long id);

    List<UserDTO> listByClinic(Long clinicId);

    List<UserDTO> listByRole(UserRole role);
}