package com.gi.userservice.repository;

import com.gi.userservice.model.entity.SecretaryProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SecretaryProfileRepository extends JpaRepository<SecretaryProfile, Long> {

    Optional<SecretaryProfile> findByUserId(Long userId);

    List<SecretaryProfile> findByClinicId(Long clinicId);
}
