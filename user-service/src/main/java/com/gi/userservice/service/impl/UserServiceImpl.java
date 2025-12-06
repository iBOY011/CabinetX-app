package com.gi.userservice.service.impl;

import com.gi.userservice.exception.BusinessException;
import com.gi.userservice.model.dto.UserDTO;
import com.gi.userservice.model.dto.request.CreateUserRequest;
import com.gi.userservice.model.entity.DoctorProfile;
import com.gi.userservice.model.entity.SecretaryProfile;
import com.gi.userservice.model.entity.User;
import com.gi.userservice.model.enums.UserRole;
import com.gi.userservice.repository.DoctorProfileRepository;
import com.gi.userservice.repository.SecretaryProfileRepository;
import com.gi.userservice.repository.UserRepository;
import com.gi.userservice.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final DoctorProfileRepository doctorProfileRepository;
    private final SecretaryProfileRepository secretaryProfileRepository;

    @Override
    @Transactional
    public UserDTO createUser(CreateUserRequest request) {
        User user = new User();
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setLogin(request.getLogin());
        user.setPassword(request.getPassword());
        user.setPhoneNumber(request.getPhoneNumber());
        user.setRole(request.getRole());
        user.setActive(true);

        User savedUser = userRepository.save(user);

        // Create profile based on role
        if (request.getRole() == UserRole.DOCTOR) {
            DoctorProfile profile = new DoctorProfile();
            profile.setUserId(savedUser.getId());
            doctorProfileRepository.save(profile);
        } else if (request.getRole() == UserRole.SECRETARY) {
            SecretaryProfile profile = new SecretaryProfile();
            profile.setUserId(savedUser.getId());
            secretaryProfileRepository.save(profile);
        }

        UserDTO dto = mapToDTO(savedUser);
        return dto;
    }

    @Override
    public UserDTO findById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        UserDTO dto = mapToDTO(user);
        return dto;
    }

    @Override
    @Transactional
    public UserDTO updateUser(Long id, UserDTO dto) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());
        user.setPassword(dto.getPassword());
        user.setPhoneNumber(dto.getPhoneNumber());
        user.setRole(dto.getRole());
        user.setActive(dto.isActive());
        User saved = userRepository.save(user);
        return mapToDTO(saved);
    }

    @Override
    @Transactional
    public UserDTO activateUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (user.isActive()) {
            throw new BusinessException("User is already active");
        }

        user.setActive(true);
        User saved = userRepository.save(user);
        UserDTO dto = mapToDTO(saved);
        return dto;
    }

    @Override
    @Transactional
    public UserDTO deactivateUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!user.isActive()) {
            throw new BusinessException("User is already inactive");
        }

        user.setActive(false);
        User saved = userRepository.save(user);
        UserDTO dto = mapToDTO(saved);
        return dto;
    }

    @Override
    public List<UserDTO> listByRole(UserRole role) {
        return userRepository.findByRole(role).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    private UserDTO mapToDTO(User user) {
        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setFirstName(user.getFirstName());
        dto.setLastName(user.getLastName());
        dto.setLogin(user.getLogin());
        dto.setPassword(user.getPassword());
        dto.setPhoneNumber(user.getPhoneNumber());
        dto.setRole(user.getRole());
        dto.setActive(user.isActive());
        return dto;
    }
}