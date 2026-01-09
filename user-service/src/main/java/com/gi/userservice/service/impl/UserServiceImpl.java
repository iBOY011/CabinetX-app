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
            // Check if clinic already has a doctor
            List<DoctorProfile> existingDoctors = doctorProfileRepository.findByClinicId(request.getClinicId());
            if (!existingDoctors.isEmpty()) {
                throw new IllegalArgumentException("Cette clinique a déjà un médecin assigné. Une clinique ne peut avoir qu'un seul médecin.");
            }
            
            DoctorProfile profile = new DoctorProfile();
            profile.setUserId(savedUser.getId());
            profile.setClinicId(request.getClinicId());
            doctorProfileRepository.save(profile);
        } else if (request.getRole() == UserRole.SECRETAIRE) {
            // Check if clinic already has a secretary
            List<SecretaryProfile> existingSecretaries = secretaryProfileRepository.findByClinicId(request.getClinicId());
            if (!existingSecretaries.isEmpty()) {
                throw new IllegalArgumentException("Cette clinique a déjà une secrétaire assignée. Une clinique ne peut avoir qu'une seule secrétaire.");
            }
            
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
        if (dto.getPassword() != null && !dto.getPassword().isEmpty()) {
            user.setPassword(dto.getPassword());
        }
        user.setPhoneNumber(dto.getPhoneNumber());
        user.setRole(dto.getRole());
        user.setActive(dto.isActive());
        User saved = userRepository.save(user);

        // Update clinicId in profile based on role
        if (dto.getClinicId() != null) {
            if (dto.getRole() == UserRole.MEDCIN) {
                // Check if another doctor is already assigned to this clinic
                List<DoctorProfile> existingDoctors = doctorProfileRepository.findByClinicId(dto.getClinicId());
                boolean clinicHasOtherDoctor = existingDoctors.stream()
                    .anyMatch(profile -> !profile.getUserId().equals(id));
                
                if (clinicHasOtherDoctor) {
                    throw new IllegalArgumentException("Cette clinique a déjà un médecin assigné. Une clinique ne peut avoir qu'un seul médecin.");
                }
                
                DoctorProfile profile = doctorProfileRepository.findByUserId(saved.getId())
                        .orElseGet(() -> {
                            DoctorProfile newProfile = new DoctorProfile();
                            newProfile.setUserId(saved.getId());
                            return newProfile;
                        });
                profile.setClinicId(dto.getClinicId());
                doctorProfileRepository.save(profile);
            } else if (dto.getRole() == UserRole.SECRETAIRE) {
                // Check if another secretary is already assigned to this clinic
                List<SecretaryProfile> existingSecretaries = secretaryProfileRepository.findByClinicId(dto.getClinicId());
                boolean clinicHasOtherSecretary = existingSecretaries.stream()
                    .anyMatch(profile -> !profile.getUserId().equals(id));
                
                if (clinicHasOtherSecretary) {
                    throw new IllegalArgumentException("Cette clinique a déjà une secrétaire assignée. Une clinique ne peut avoir qu'une seule secrétaire.");
                }
                
                SecretaryProfile profile = secretaryProfileRepository.findByUserId(saved.getId())
                        .orElseGet(() -> {
                            SecretaryProfile newProfile = new SecretaryProfile();
                            newProfile.setUserId(saved.getId());
                            return newProfile;
                        });
                profile.setClinicId(dto.getClinicId());
                secretaryProfileRepository.save(profile);
            }
        }

        UserDTO resultDto = mapToDTO(saved);
        resultDto.setClinicId(dto.getClinicId());
        return resultDto;
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

    @Override
    public List<UserDTO> findByCabinetIdAndRole(Long cabinetId, String role) {
        UserRole userRole;
        try {
            userRole = UserRole.valueOf(role.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Invalid role: " + role);
        }

        List<Long> userIds;
        
        // Query profiles by clinicId to get user IDs
        if (userRole == UserRole.MEDCIN) {
            userIds = doctorProfileRepository.findByClinicId(cabinetId).stream()
                    .map(DoctorProfile::getUserId)
                    .collect(Collectors.toList());
        } else if (userRole == UserRole.SECRETAIRE) {
            userIds = secretaryProfileRepository.findByClinicId(cabinetId).stream()
                    .map(SecretaryProfile::getUserId)
                    .collect(Collectors.toList());
        } else {
            throw new RuntimeException("Role " + role + " does not have clinic association");
        }

        // Fetch users by IDs and filter by role
        return userRepository.findAllById(userIds).stream()
                .filter(user -> user.getRole() == userRole)
                .map(user -> {
                    UserDTO dto = mapToDTO(user);
                    dto.setClinicId(cabinetId);
                    return dto;
                })
                .collect(Collectors.toList());
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