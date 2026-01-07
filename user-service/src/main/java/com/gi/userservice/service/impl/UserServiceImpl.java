package com.gi.userservice.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gi.userservice.keycloak.KeycloakAdminClient;
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

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final DoctorProfileRepository doctorProfileRepository;
    private final SecretaryProfileRepository secretaryProfileRepository;
    private final KeycloakAdminClient keycloakAdminClient;

    @Override
    @Transactional
    public UserDTO createUser(CreateUserRequest request) {
        String keycloakUserId = null;
        if (keycloakAdminClient.isEnabled()) {
            keycloakUserId = keycloakAdminClient.createUser(request);
        }

        User user = new User();
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setLogin(request.getLogin());
        user.setPassword(request.getPassword());
        user.setPhoneNumber(request.getPhoneNumber());
        user.setRole(request.getRole());
        user.setActive(true);
        user.setKeycloakUserId(keycloakUserId);

        User savedUser;
        try {
            savedUser = userRepository.save(user);
        } catch (RuntimeException ex) {
            keycloakAdminClient.deleteUser(keycloakUserId);
            throw ex;
        }

        // Create profile based on role
        if (request.getRole() == UserRole.MEDCIN) {
            DoctorProfile profile = new DoctorProfile();
            profile.setUserId(savedUser.getId());
            profile.setClinicId(request.getClinicId()); // Assuming clinicId in request
            doctorProfileRepository.save(profile);
        } else if (request.getRole() == UserRole.SECRETAIRE) {
            SecretaryProfile profile = new SecretaryProfile();
            profile.setUserId(savedUser.getId());
            profile.setClinicId(request.getClinicId());
            secretaryProfileRepository.save(profile);
        }

        UserDTO dto = mapToDTO(savedUser);
        dto.setClinicId(request.getClinicId());
        return dto;
    }

    @Override
    public UserDTO findById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        UserDTO dto = mapToDTO(user);
        // Set clinicId from profile
        if (user.getRole() == UserRole.MEDCIN) {
            doctorProfileRepository.findByUserId(user.getId())
                    .ifPresent(profile -> dto.setClinicId(profile.getClinicId()));
        } else if (user.getRole() == UserRole.SECRETAIRE) {
            secretaryProfileRepository.findByUserId(user.getId())
                    .ifPresent(profile -> dto.setClinicId(profile.getClinicId()));
        }
        return dto;
    }

    @Override
    public List<UserDTO> findAll() {
        return userRepository.findAll().stream()
                .map(user -> {
                    UserDTO dto = mapToDTO(user);
                    // Set clinicId from profile
                    if (user.getRole() == UserRole.MEDCIN) {
                        doctorProfileRepository.findByUserId(user.getId())
                                .ifPresent(profile -> dto.setClinicId(profile.getClinicId()));
                    } else if (user.getRole() == UserRole.SECRETAIRE) {
                        secretaryProfileRepository.findByUserId(user.getId())
                                .ifPresent(profile -> dto.setClinicId(profile.getClinicId()));
                    }
                    return dto;
                })
                .collect(Collectors.toList());
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
        user.setActive(true);
        User saved = userRepository.save(user);
        UserDTO dto = mapToDTO(saved);
        // Set clinicId from profile
        if (user.getRole() == UserRole.MEDCIN) {
            doctorProfileRepository.findByUserId(user.getId())
                    .ifPresent(profile -> dto.setClinicId(profile.getClinicId()));
        } else if (user.getRole() == UserRole.SECRETAIRE) {
            secretaryProfileRepository.findByUserId(user.getId())
                    .ifPresent(profile -> dto.setClinicId(profile.getClinicId()));
        }
        return dto;
    }

    @Override
    @Transactional
    public UserDTO deactivateUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setActive(false);
        User saved = userRepository.save(user);
        UserDTO dto = mapToDTO(saved);
        // Set clinicId from profile
        if (user.getRole() == UserRole.MEDCIN) {
            doctorProfileRepository.findByUserId(user.getId())
                    .ifPresent(profile -> dto.setClinicId(profile.getClinicId()));
        } else if (user.getRole() == UserRole.SECRETAIRE) {
            secretaryProfileRepository.findByUserId(user.getId())
                    .ifPresent(profile -> dto.setClinicId(profile.getClinicId()));
        }
        return dto;
    }

    @Override
    public List<UserDTO> listByClinic(Long clinicId) {
        // Assuming we query profiles for clinic
        List<Long> userIds = doctorProfileRepository.findByClinicId(clinicId).stream()
                .map(DoctorProfile::getUserId)
                .collect(Collectors.toList());
        userIds.addAll(secretaryProfileRepository.findByClinicId(clinicId).stream()
                .map(SecretaryProfile::getUserId)
                .collect(Collectors.toList()));

        return userRepository.findAllById(userIds).stream()
                .map(user -> {
                    UserDTO dto = mapToDTO(user);
                    dto.setClinicId(clinicId);
                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<UserDTO> listByRole(UserRole role) {
        return userRepository.findByRole(role).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public UserDTO findByLogin(String login) {
        User user = userRepository.findByLogin(login)
                .orElseThrow(() -> new RuntimeException("User not found with login: " + login));
        UserDTO dto = mapToDTO(user);

        // Set clinicId from profile based on role
        if (user.getRole() == UserRole.MEDCIN) {
            doctorProfileRepository.findByUserId(user.getId())
                    .ifPresent(profile -> dto.setClinicId(profile.getClinicId()));
        } else if (user.getRole() == UserRole.SECRETAIRE) {
            secretaryProfileRepository.findByUserId(user.getId())
                    .ifPresent(profile -> dto.setClinicId(profile.getClinicId()));
        }

        return dto;
    }

    private UserDTO mapToDTO(User user) {
        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setKeycloakUserId(user.getKeycloakUserId());
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