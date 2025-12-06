package com.gi.userservice.service;

import com.gi.userservice.model.dto.UserDTO;
import com.gi.userservice.model.dto.request.CreateUserRequest;
import com.gi.userservice.model.enums.UserRole;

import java.util.List;

public interface UserService {

    UserDTO createUser(CreateUserRequest request);

    UserDTO findById(Long id);

    UserDTO updateUser(Long id, UserDTO dto);

    UserDTO activateUser(Long id);

    UserDTO deactivateUser(Long id);

    List<UserDTO> listByRole(UserRole role);
}